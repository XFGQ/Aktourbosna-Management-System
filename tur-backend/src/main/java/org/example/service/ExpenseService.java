package org.example.service;

import org.example.application.dto.expense.ExpenseCreateDTO;
import org.example.application.dto.expense.ExpenseResponseDTO;
import org.example.application.dto.expense.ExpenseUpdateDTO;
import org.example.application.mapper.ExpenseMapper;
import org.example.model.Expense;
import org.example.model.Tour;
import org.example.repository.ExpenseRepository;
import org.example.repository.TourRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseMapper expenseMapper;
    private final TourRepository tourRepository;

    public ExpenseService(ExpenseRepository expenseRepository, ExpenseMapper expenseMapper,
                          TourRepository tourRepository) {
        this.expenseRepository = expenseRepository;
        this.expenseMapper = expenseMapper;
        this.tourRepository = tourRepository;
    }

    public List<ExpenseResponseDTO> getExpensesByTour(Long tourId) {
        return expenseRepository.findByTour_TourId(tourId).stream()
                .map(expenseMapper::toResponse)
                .collect(Collectors.toList());
    }

    public ExpenseResponseDTO getExpenseById(Long id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Masraf bulunamadı. ID: " + id));
        return expenseMapper.toResponse(expense);
    }

    public ExpenseResponseDTO createExpense(Long tourId, ExpenseCreateDTO dto) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new RuntimeException("Tur bulunamadı. ID: " + tourId));
        Expense expense = expenseMapper.toEntity(dto);
        expense.setTour(tour);
        return expenseMapper.toResponse(expenseRepository.save(expense));
    }

    public ExpenseResponseDTO updateExpense(Long id, ExpenseUpdateDTO dto) {
        Expense existing = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Güncellenecek masraf bulunamadı. ID: " + id));
        expenseMapper.updateEntityFromDto(dto, existing);
        return expenseMapper.toResponse(expenseRepository.save(existing));
    }

    public void deleteExpense(Long id) {
        if (!expenseRepository.existsById(id)) {
            throw new RuntimeException("Silinecek masraf bulunamadı. ID: " + id);
        }
        expenseRepository.deleteById(id);
    }
}

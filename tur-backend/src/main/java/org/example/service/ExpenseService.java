package org.example.service;

import org.example.application.dto.ExpenseDTO;
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

    public List<ExpenseDTO> getExpensesByTour(Long tourId) {
        return expenseRepository.findByTour_TourId(tourId).stream()
                .map(expenseMapper::toDto)
                .collect(Collectors.toList());
    }

    public ExpenseDTO getExpenseById(Long id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Masraf bulunamadı. ID: " + id));
        return expenseMapper.toDto(expense);
    }

    public ExpenseDTO createExpense(Long tourId, ExpenseDTO dto) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new RuntimeException("Tur bulunamadı. ID: " + tourId));
        Expense expense = expenseMapper.toEntity(dto);
        expense.setTour(tour);
        return expenseMapper.toDto(expenseRepository.save(expense));
    }

    public ExpenseDTO updateExpense(Long id, ExpenseDTO dto) {
        Expense existing = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Güncellenecek masraf bulunamadı. ID: " + id));
        expenseMapper.updateEntityFromDto(dto, existing);
        return expenseMapper.toDto(expenseRepository.save(existing));
    }

    public void deleteExpense(Long id) {
        if (!expenseRepository.existsById(id)) {
            throw new RuntimeException("Silinecek masraf bulunamadı. ID: " + id);
        }
        expenseRepository.deleteById(id);
    }
}

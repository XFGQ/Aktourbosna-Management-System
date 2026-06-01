package org.example.service;

import org.example.application.dto.expense.ExpenseCreateDTO;
import org.example.application.dto.expense.ExpenseResponseDTO;
import org.example.application.dto.expense.ExpenseUpdateDTO;
import org.example.application.mapper.ExpenseMapper;
import org.example.model.Expense;
import org.example.model.Tour;
import org.example.repository.ExpenseRepository;
import org.example.repository.TourRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseMapper expenseMapper;
    private final TourRepository tourRepository;
    private final FileStorageService fileStorageService;

    public ExpenseService(ExpenseRepository expenseRepository, ExpenseMapper expenseMapper,
                          TourRepository tourRepository, FileStorageService fileStorageService) {
        this.expenseRepository = expenseRepository;
        this.expenseMapper = expenseMapper;
        this.tourRepository = tourRepository;
        this.fileStorageService = fileStorageService;
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

    @Transactional
    public ExpenseResponseDTO uploadReceipt(Long expenseId, MultipartFile file) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Masraf bulunamadı. ID: " + expenseId));

        if (expense.getReceiptPath() != null && !expense.getReceiptPath().isEmpty()) {
            try {
                fileStorageService.deleteFile(expense.getReceiptPath());
            } catch (Exception ignored) {}
        }

        String subFolder = "tour_" + expense.getTour().getTourId();
        String filePath = fileStorageService.storeFile(file, subFolder);
        
        expense.setReceiptPath(filePath);
        return expenseMapper.toResponse(expenseRepository.save(expense));
    }

    @Transactional
    public ExpenseResponseDTO deleteReceipt(Long expenseId) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Masraf bulunamadı. ID: " + expenseId));

        if (expense.getReceiptPath() != null && !expense.getReceiptPath().isEmpty()) {
            fileStorageService.deleteFile(expense.getReceiptPath());
            expense.setReceiptPath(null);
            expense = expenseRepository.save(expense);
        }
        return expenseMapper.toResponse(expense);
    }

    public Resource loadReceiptAsResource(Long expenseId) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Masraf bulunamadı. ID: " + expenseId));
        
        if (expense.getReceiptPath() == null || expense.getReceiptPath().isEmpty()) {
            throw new RuntimeException("Masraf için makbuz bulunamadı.");
        }

        try {
            Path filePath = fileStorageService.loadFileAsResource(expense.getReceiptPath());
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("Dosya bulunamadı.");
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("Dosya bulunamadı.", ex);
        }
    }
}

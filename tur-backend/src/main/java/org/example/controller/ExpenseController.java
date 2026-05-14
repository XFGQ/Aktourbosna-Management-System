package org.example.controller;

import jakarta.validation.Valid;
import org.example.application.dto.expense.ExpenseCreateDTO;
import org.example.application.dto.expense.ExpenseResponseDTO;
import org.example.application.dto.expense.ExpenseUpdateDTO;
import org.example.service.ExpenseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tours/{tourId}/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping
    public ResponseEntity<List<ExpenseResponseDTO>> getExpensesByTour(@PathVariable Long tourId) {
        return ResponseEntity.ok(expenseService.getExpensesByTour(tourId));
    }

    @GetMapping("/{expenseId}")
    public ResponseEntity<ExpenseResponseDTO> getExpenseById(@PathVariable Long tourId,
                                                             @PathVariable Long expenseId) {
        return ResponseEntity.ok(expenseService.getExpenseById(expenseId));
    }

    @PostMapping
    public ResponseEntity<ExpenseResponseDTO> createExpense(@PathVariable Long tourId,
                                                            @Valid @RequestBody ExpenseCreateDTO dto) {
        return ResponseEntity.ok(expenseService.createExpense(tourId, dto));
    }

    @PutMapping("/{expenseId}")
    public ResponseEntity<ExpenseResponseDTO> updateExpense(@PathVariable Long tourId,
                                                            @PathVariable Long expenseId,
                                                            @RequestBody ExpenseUpdateDTO dto) {
        return ResponseEntity.ok(expenseService.updateExpense(expenseId, dto));
    }

    @DeleteMapping("/{expenseId}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long tourId,
                                              @PathVariable Long expenseId) {
        expenseService.deleteExpense(expenseId);
        return ResponseEntity.noContent().build();
    }
}

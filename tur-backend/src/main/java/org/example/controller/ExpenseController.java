package org.example.controller;

import org.example.application.dto.ExpenseDTO;
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
    public ResponseEntity<List<ExpenseDTO>> getExpensesByTour(@PathVariable Long tourId) {
        return ResponseEntity.ok(expenseService.getExpensesByTour(tourId));
    }

    @PostMapping
    public ResponseEntity<ExpenseDTO> createExpense(@PathVariable Long tourId,
                                                    @RequestBody ExpenseDTO dto) {
        return ResponseEntity.ok(expenseService.createExpense(tourId, dto));
    }

    @PutMapping("/{expenseId}")
    public ResponseEntity<ExpenseDTO> updateExpense(@PathVariable Long tourId,
                                                    @PathVariable Long expenseId,
                                                    @RequestBody ExpenseDTO dto) {
        return ResponseEntity.ok(expenseService.updateExpense(expenseId, dto));
    }

    @DeleteMapping("/{expenseId}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long tourId,
                                              @PathVariable Long expenseId) {
        expenseService.deleteExpense(expenseId);
        return ResponseEntity.noContent().build();
    }
}

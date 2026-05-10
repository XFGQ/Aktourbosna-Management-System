package org.example.service;

import org.example.model.Expense;

import java.util.List;

public class ExpenseService {

    private final ApiService apiService = new ApiService();

    public List<Expense> getExpensesByTourId(Long tourId) throws Exception {
        return apiService.fetchExpensesByTour(tourId);
    }

    public Double calculateTotal(List<Expense> expenses) {
        if (expenses == null) return 0.0;
        return expenses.stream()
                .mapToDouble(e -> e.getAmount() != null ? e.getAmount() : 0.0)
                .sum();
    }

    public Expense addExpense(Long tourId, Expense expense) throws Exception {
        return apiService.createExpense(tourId, expense);
    }

    public void deleteExpense(Long tourId, Long expenseId) throws Exception {
        apiService.deleteExpense(tourId, expenseId);
    }

    public Double calculateTotalForCategory(List<Expense> expenses, String category) {
        if (expenses == null) return 0.0;
        return expenses.stream()
                .filter(e -> category.equals(e.getCategory()))
                .mapToDouble(e -> e.getAmount() != null ? e.getAmount() : 0.0)
                .sum();
    }
}

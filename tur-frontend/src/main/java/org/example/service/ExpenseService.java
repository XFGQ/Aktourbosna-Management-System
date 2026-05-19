package org.example.service;

import org.example.model.Expense;

import java.util.List;

public class ExpenseService {

    private final ApiService apiService = new ApiService();

    public List<Expense> getExpensesByTourId(Long tourId) throws Exception {
        return apiService.fetchExpensesByTour(tourId);
    }

    public Expense addExpense(Long tourId, Expense expense) throws Exception {
        Expense result = apiService.createExpense(tourId, expense);
        TourService.invalidateCache();
        return result;
    }

    public Expense updateExpense(Long tourId, Long expenseId, Expense expense) throws Exception {
        Expense result = apiService.updateExpense(tourId, expenseId, expense);
        TourService.invalidateCache();
        return result;
    }

    public void deleteExpense(Long tourId, Long expenseId) throws Exception {
        apiService.deleteExpense(tourId, expenseId);
        TourService.invalidateCache();
    }

    public double calculateTotal(List<Expense> expenses) {
        if (expenses == null) return 0.0;
        return expenses.stream()
                .mapToDouble(e -> e.getAmount() != null ? e.getAmount() : 0.0)
                .sum();
    }
}

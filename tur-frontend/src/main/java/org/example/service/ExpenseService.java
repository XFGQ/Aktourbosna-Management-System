package org.example.service;

import org.example.model.Expense;
import org.example.model.Tour;

import java.util.List;

public class ExpenseService {

    public List<Expense> getExpensesForTour(Tour tour) {
        return tour.getExpenses();
    }

    public Double calculateTotal(List<Expense> expenses) {
        return expenses.stream()
                .mapToDouble(e -> e.getAmount() != null ? e.getAmount() : 0.0)
                .sum();
    }

    public Double calculateTotalForCategory(List<Expense> expenses, String category) {
        return expenses.stream()
                .filter(e -> category.equals(e.getCategory()))
                .mapToDouble(e -> e.getAmount() != null ? e.getAmount() : 0.0)
                .sum();
    }
}
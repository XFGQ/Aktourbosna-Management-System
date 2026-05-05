package org.example.model;

import java.time.LocalDate;

public class Expense {

    private Long expenseId;
    private String category;
    private Float amount;
    private LocalDate date;
    private String receiptPath;

    public Expense() {}

    public Long getExpenseId() { return expenseId; }
    public void setExpenseId(Long expenseId) { this.expenseId = expenseId; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Float getAmount() { return amount; }
    public void setAmount(Float amount) { this.amount = amount; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getReceiptPath() { return receiptPath; }
    public void setReceiptPath(String receiptPath) { this.receiptPath = receiptPath; }
}
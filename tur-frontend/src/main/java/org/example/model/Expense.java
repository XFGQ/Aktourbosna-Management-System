package org.example.model;

public class Expense {

    private Long id;
    private Tour tour;
    private String category;
    private Double amount;
    private String date;
    private String receiptPath;
    private String expenseName;

    public Expense() {}

    public Expense(String category, Double amount, String date,
                   String receiptPath, String expenseName) {
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.receiptPath = receiptPath;
        this.expenseName = expenseName;
    }

    public void uploadReceipt() {}

    public String getDetails() {
        return "Category: " + category + " | Amount: " + amount + " | Name: " + expenseName;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Tour getTour() { return tour; }
    public void setTour(Tour tour) { this.tour = tour; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getReceiptPath() { return receiptPath; }
    public void setReceiptPath(String receiptPath) { this.receiptPath = receiptPath; }
    public String getExpenseName() { return expenseName; }
    public void setExpenseName(String expenseName) { this.expenseName = expenseName; }
}
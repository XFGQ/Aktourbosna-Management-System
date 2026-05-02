package org.example.model;

import java.util.ArrayList;
import java.util.List;

public class Tour {

    private Long tourId;
    private String tourName;
    private String startDate;
    private String hotelName;
    private String destination;
    private String departureCity;
    private String vehicle;
    private String guideName;
    private String groupSize;
    private String status;
    private Double totalPrice;

    private List<Expense> expenses = new ArrayList<>();
    private List<Customer> customers = new ArrayList<>();

    public Tour() {}

    // DashboardController
    public Tour(String tourName, String destination, String startDate) {
        this.tourName = tourName;
        this.destination = destination;
        this.startDate = startDate;
    }

    // GuideDashboardController
    public Tour(String tourName, String destination, String startDate, String hotelName) {
        this.tourName = tourName;
        this.destination = destination;
        this.startDate = startDate;
        this.hotelName = hotelName;
    }

    // TourManagementController
    public Tour(String tourName, String destination, String startDate,
                String hotelName, Double totalPrice, String status) {
        this.tourName = tourName;
        this.destination = destination;
        this.startDate = startDate;
        this.hotelName = hotelName;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    // GuideTourManagementController
    public Tour(String tourName, String destination, String startDate,
                String hotelName, String groupSize, String status) {
        this.tourName = tourName;
        this.destination = destination;
        this.startDate = startDate;
        this.hotelName = hotelName;
        this.groupSize = groupSize;
        this.status = status;
    }

    public void addExpense(Expense expense) {
        if (this.expenses == null) this.expenses = new ArrayList<>();
        this.expenses.add(expense);
    }

    public void addCustomer(Customer customer) {
        if (this.customers == null) this.customers = new ArrayList<>();
        this.customers.add(customer);
    }

    public Double calculateCost() {
        double total = 0;
        for (Expense e : expenses) total += e.getAmount();
        return total;
    }

    // ── Getter / Setter ───────────────────────────────────────────────────────
    public Long getTourId() { return tourId; }
    public void setTourId(Long tourId) { this.tourId = tourId; }
    public String getTourName() { return tourName; }
    public void setTourName(String tourName) { this.tourName = tourName; }
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public String getHotelName() { return hotelName; }
    public void setHotelName(String hotelName) { this.hotelName = hotelName; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public String getDepartureCity() { return departureCity; }
    public void setDepartureCity(String departureCity) { this.departureCity = departureCity; }
    public String getVehicle() { return vehicle; }
    public void setVehicle(String vehicle) { this.vehicle = vehicle; }
    public String getGuideName() { return guideName; }
    public void setGuideName(String guideName) { this.guideName = guideName; }
    public String getGroupSize() { return groupSize; }
    public void setGroupSize(String groupSize) { this.groupSize = groupSize; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }
    public List<Expense> getExpenses() { return expenses; }
    public List<Customer> getCustomers() { return customers; }
}
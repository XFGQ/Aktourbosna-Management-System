package org.example.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Tour {

    // Backend alanları
    private Long tourId;
    private String tourName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String hotelName;
    private Double calculatedPrice = 0.0;
    private Double finalPrice = 0.0;
    private Guide guide;
    private Vehicle vehicle;
    private Route baseRoute;
    private List<Waypoint> extraWaypoints = new ArrayList<>();
    private List<Customer> customers = new ArrayList<>();
    private List<Expense> expenses = new ArrayList<>();

    // UI alanları (frontend gösterimi için)
    private String destination;
    private String departureCity;
    private String groupSize;
    private String status;

    public Tour() {}

    public Long getTourId() { return tourId; }
    public void setTourId(Long tourId) { this.tourId = tourId; }
    public String getTourName() { return tourName; }
    public void setTourName(String tourName) { this.tourName = tourName; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public String getHotelName() { return hotelName; }
    public void setHotelName(String hotelName) { this.hotelName = hotelName; }
    public Double getCalculatedPrice() { return calculatedPrice; }
    public void setCalculatedPrice(Double calculatedPrice) { this.calculatedPrice = calculatedPrice; }
    public Double getFinalPrice() { return finalPrice; }
    public void setFinalPrice(Double finalPrice) { this.finalPrice = finalPrice; }
    public Guide getGuide() { return guide; }
    public void setGuide(Guide guide) { this.guide = guide; }
    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }
    public Route getBaseRoute() { return baseRoute; }
    public void setBaseRoute(Route baseRoute) { this.baseRoute = baseRoute; }
    public List<Waypoint> getExtraWaypoints() { return extraWaypoints; }
    public void setExtraWaypoints(List<Waypoint> extraWaypoints) { this.extraWaypoints = extraWaypoints; }
    public List<Customer> getCustomers() { return customers; }
    public void setCustomers(List<Customer> customers) { this.customers = customers; }
    public List<Expense> getExpenses() { return expenses; }
    public void setExpenses(List<Expense> expenses) { this.expenses = expenses; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public String getDepartureCity() { return departureCity; }
    public void setDepartureCity(String departureCity) { this.departureCity = departureCity; }
    public String getGroupSize() { return groupSize; }
    public void setGroupSize(String groupSize) { this.groupSize = groupSize; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
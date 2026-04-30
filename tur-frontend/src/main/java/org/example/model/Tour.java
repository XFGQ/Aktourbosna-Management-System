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
    private Float totalCost = 0.0f;
    private String hotelName;
    private Guide guide;
    private Vehicle vehicle;
    private List<Route> routes = new ArrayList<>();
    private List<TourWaypoint> tourWaypoints = new ArrayList<>();
    private List<Booking> bookings = new ArrayList<>();
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
    public Float getTotalCost() { return totalCost; }
    public void setTotalCost(Float totalCost) { this.totalCost = totalCost; }
    public String getHotelName() { return hotelName; }
    public void setHotelName(String hotelName) { this.hotelName = hotelName; }
    public Guide getGuide() { return guide; }
    public void setGuide(Guide guide) { this.guide = guide; }
    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }
    public List<Route> getRoutes() { return routes; }
    public void setRoutes(List<Route> routes) { this.routes = routes; }
    public List<TourWaypoint> getTourWaypoints() { return tourWaypoints; }
    public void setTourWaypoints(List<TourWaypoint> tourWaypoints) { this.tourWaypoints = tourWaypoints; }
    public List<Booking> getBookings() { return bookings; }
    public void setBookings(List<Booking> bookings) { this.bookings = bookings; }
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
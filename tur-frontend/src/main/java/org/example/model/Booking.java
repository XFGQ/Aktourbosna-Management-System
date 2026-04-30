package org.example.model;

import java.util.ArrayList;
import java.util.List;

public class Booking {

    private Long id;
    private Tour tour;
    private String leadCustomerName;
    private Integer guestCount;
    private BookingStatus bookingStatus;
    private Long hotelId;
    private List<Customer> customers = new ArrayList<>();

    public enum BookingStatus {
        PENDING, CONFIRMED, CANCELLED
    }

    public Booking() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Tour getTour() { return tour; }
    public void setTour(Tour tour) { this.tour = tour; }
    public String getLeadCustomerName() { return leadCustomerName; }
    public void setLeadCustomerName(String leadCustomerName) { this.leadCustomerName = leadCustomerName; }
    public Integer getGuestCount() { return guestCount; }
    public void setGuestCount(Integer guestCount) { this.guestCount = guestCount; }
    public BookingStatus getBookingStatus() { return bookingStatus; }
    public void setBookingStatus(BookingStatus bookingStatus) { this.bookingStatus = bookingStatus; }
    public Long getHotelId() { return hotelId; }
    public void setHotelId(Long hotelId) { this.hotelId = hotelId; }
    public List<Customer> getCustomers() { return customers; }
    public void setCustomers(List<Customer> customers) { this.customers = customers; }
}
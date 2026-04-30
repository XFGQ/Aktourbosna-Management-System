package org.example.service;

import org.example.model.Booking;
import org.example.model.Customer;

import java.util.List;

public class CustomerService {

    public List<Customer> getCustomersForBooking(Booking booking) {
        return booking.getCustomers();
    }

    public long countCustomers(List<Customer> customers) {
        return customers.size();
    }
}
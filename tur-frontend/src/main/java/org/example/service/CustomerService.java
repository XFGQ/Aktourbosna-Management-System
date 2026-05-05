package org.example.service;

import org.example.model.Customer;
import org.example.model.Tour;

import java.util.List;

public class CustomerService {

    public List<Customer> getCustomersForTour(Tour tour) {
        return tour.getCustomers();
    }

    public long countCustomers(List<Customer> customers) {
        return customers.size();
    }

    public String getDisplayName(Customer customer) {
        return customer != null ? customer.getFullName() : "";
    }
}
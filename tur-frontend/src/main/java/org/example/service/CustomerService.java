package org.example.service;

import org.example.model.Customer;

import java.util.List;

public class CustomerService {

    public long countCustomers(List<Customer> customers) {
        return customers != null ? customers.size() : 0;
    }

    public String getDisplayName(Customer customer) {
        return customer != null ? customer.getFullName() : "";
    }
}

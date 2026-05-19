package org.example.service;

import org.example.model.Customer;

import java.util.List;

public class CustomerService {

    private final ApiService apiService = new ApiService();

    public List<Customer> getCustomers(Long tourId) throws Exception {
        return apiService.fetchCustomers(tourId);
    }

    public Customer addCustomer(Long tourId, Customer customer) throws Exception {
        return apiService.createCustomer(tourId, customer);
    }

    public void deleteCustomer(Long tourId, Long customerId) throws Exception {
        apiService.deleteCustomer(tourId, customerId);
    }
}

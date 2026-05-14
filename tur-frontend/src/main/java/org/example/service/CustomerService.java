package org.example.service;

import org.example.model.Customer;

import java.util.List;

public class CustomerService {

    private final ApiService api = new ApiService();

    public List<Customer> getCustomersByTour(Long tourId) throws Exception {
        return api.fetchCustomersByTour(tourId);
    }

    public Customer addCustomer(Long tourId, Customer customer) throws Exception {
        return api.createCustomer(tourId, customer);
    }

    public void deleteCustomer(Long tourId, Long customerId) throws Exception {
        api.deleteCustomer(tourId, customerId);
    }

    public long countCustomers(List<Customer> customers) {
        return customers != null ? customers.size() : 0;
    }

    public String getDisplayName(Customer customer) {
        return customer != null ? customer.getFullName() : "";
    }
}

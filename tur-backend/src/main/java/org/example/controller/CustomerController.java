package org.example.controller;

import jakarta.validation.Valid;
import org.example.application.dto.customer.CustomerCreateDTO;
import org.example.application.dto.customer.CustomerResponseDTO;
import org.example.application.dto.customer.CustomerUpdateDTO;
import org.example.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tours/{tourId}/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> getCustomersByTour(@PathVariable Long tourId) {
        return ResponseEntity.ok(customerService.getCustomersByTour(tourId));
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerResponseDTO> getCustomerById(@PathVariable Long tourId,
                                                               @PathVariable Long customerId) {
        return ResponseEntity.ok(customerService.getCustomerById(customerId));
    }

    @PostMapping
    public ResponseEntity<CustomerResponseDTO> createCustomer(@PathVariable Long tourId,
                                                              @Valid @RequestBody CustomerCreateDTO dto) {
        return ResponseEntity.ok(customerService.createCustomer(tourId, dto));
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<CustomerResponseDTO> updateCustomer(@PathVariable Long tourId,
                                                              @PathVariable Long customerId,
                                                              @RequestBody CustomerUpdateDTO dto) {
        return ResponseEntity.ok(customerService.updateCustomer(customerId, dto));
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long tourId,
                                               @PathVariable Long customerId) {
        customerService.deleteCustomer(customerId);
        return ResponseEntity.noContent().build();
    }
}

package org.example.service;

import org.example.application.dto.CustomerDTO;
import org.example.application.mapper.CustomerMapper;
import org.example.model.Customer;
import org.example.model.Tour;
import org.example.repository.CustomerRepository;
import org.example.repository.TourRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final TourRepository tourRepository;

    public CustomerService(CustomerRepository customerRepository, CustomerMapper customerMapper,
                           TourRepository tourRepository) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
        this.tourRepository = tourRepository;
    }

    public List<CustomerDTO> getCustomersByTour(Long tourId) {
        return customerRepository.findByTour_TourId(tourId).stream()
                .map(customerMapper::toDto)
                .collect(Collectors.toList());
    }

    public CustomerDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Müşteri bulunamadı. ID: " + id));
        return customerMapper.toDto(customer);
    }

    public CustomerDTO createCustomer(Long tourId, CustomerDTO dto) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new RuntimeException("Tur bulunamadı. ID: " + tourId));
        Customer customer = customerMapper.toEntity(dto);
        customer.setTour(tour);
        return customerMapper.toDto(customerRepository.save(customer));
    }

    public CustomerDTO updateCustomer(Long id, CustomerDTO dto) {
        Customer existing = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Güncellenecek müşteri bulunamadı. ID: " + id));
        customerMapper.updateEntityFromDto(dto, existing);
        return customerMapper.toDto(customerRepository.save(existing));
    }

    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new RuntimeException("Silinecek müşteri bulunamadı. ID: " + id);
        }
        customerRepository.deleteById(id);
    }
}

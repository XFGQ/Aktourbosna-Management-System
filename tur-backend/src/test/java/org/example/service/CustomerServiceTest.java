package org.example.service;

import org.example.application.dto.customer.CustomerCreateDTO;
import org.example.application.dto.customer.CustomerResponseDTO;
import org.example.application.dto.customer.CustomerUpdateDTO;
import org.example.application.mapper.CustomerMapper;
import org.example.model.Customer;
import org.example.model.Tour;
import org.example.repository.CustomerRepository;
import org.example.repository.TourRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock CustomerRepository customerRepository;
    @Mock CustomerMapper customerMapper;
    @Mock TourRepository tourRepository;
    @InjectMocks CustomerService customerService;

    private Customer customer;
    private CustomerResponseDTO responseDTO;

    @BeforeEach
    void setup() {
        Tour tour = new Tour();
        tour.setTourId(1L);

        customer = new Customer();
        customer.setId(1L);
        customer.setFullName("John Doe");
        customer.setPassportNumber("AB12345");
        customer.setTour(tour);

        responseDTO = new CustomerResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setFullName("John Doe");
        responseDTO.setTourId(1L);
    }

    @Test
    void getCustomersByTour_returnsFilteredList() {
        when(customerRepository.findByTour_TourId(1L)).thenReturn(List.of(customer));
        when(customerMapper.toResponse(customer)).thenReturn(responseDTO);

        List<CustomerResponseDTO> result = customerService.getCustomersByTour(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFullName()).isEqualTo("John Doe");
    }

    @Test
    void getCustomersByTour_noCustomers_returnsEmptyList() {
        when(customerRepository.findByTour_TourId(99L)).thenReturn(List.of());

        List<CustomerResponseDTO> result = customerService.getCustomersByTour(99L);

        assertThat(result).isEmpty();
    }

    @Test
    void getCustomerById_found_returnsDTO() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerMapper.toResponse(customer)).thenReturn(responseDTO);

        CustomerResponseDTO result = customerService.getCustomerById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getFullName()).isEqualTo("John Doe");
    }

    @Test
    void getCustomerById_notFound_throwsRuntimeException() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.getCustomerById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");
    }

    @Test
    void createCustomer_tourExists_savesAndReturnsDTO() {
        CustomerCreateDTO dto = new CustomerCreateDTO();
        dto.setFullName("Jane Smith");
        dto.setPassportNumber("CD67890");

        Tour tour = new Tour();
        tour.setTourId(1L);

        when(tourRepository.findById(1L)).thenReturn(Optional.of(tour));
        when(customerMapper.toEntity(dto)).thenReturn(customer);
        when(customerRepository.save(customer)).thenReturn(customer);
        when(customerMapper.toResponse(customer)).thenReturn(responseDTO);

        CustomerResponseDTO result = customerService.createCustomer(1L, dto);

        assertThat(result.getFullName()).isEqualTo("John Doe");
        verify(customerRepository).save(customer);
    }

    @Test
    void createCustomer_tourNotFound_throwsRuntimeException() {
        when(tourRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.createCustomer(99L, new CustomerCreateDTO()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");
    }

    @Test
    void updateCustomer_found_updatesAndReturns() {
        CustomerUpdateDTO dto = new CustomerUpdateDTO();
        dto.setFullName("John Updated");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.save(customer)).thenReturn(customer);
        when(customerMapper.toResponse(customer)).thenReturn(responseDTO);

        CustomerResponseDTO result = customerService.updateCustomer(1L, dto);

        assertThat(result).isEqualTo(responseDTO);
        verify(customerMapper).updateEntityFromDto(dto, customer);
    }

    @Test
    void updateCustomer_notFound_throwsRuntimeException() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.updateCustomer(99L, new CustomerUpdateDTO()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");
    }

    @Test
    void deleteCustomer_exists_deletesSuccessfully() {
        when(customerRepository.existsById(1L)).thenReturn(true);

        customerService.deleteCustomer(1L);

        verify(customerRepository).deleteById(1L);
    }

    @Test
    void deleteCustomer_notFound_throwsRuntimeException() {
        when(customerRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> customerService.deleteCustomer(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");
    }
}

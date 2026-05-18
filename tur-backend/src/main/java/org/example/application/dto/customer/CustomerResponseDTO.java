package org.example.application.dto.customer;

import lombok.Data;

@Data
public class CustomerResponseDTO {

    private Long id;
    private String fullName;
    private String passportNumber;
    private String phone;
    private String nationality;
    private Long tourId;
}

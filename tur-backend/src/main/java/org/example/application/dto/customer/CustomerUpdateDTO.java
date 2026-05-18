package org.example.application.dto.customer;

import lombok.Data;

@Data
public class CustomerUpdateDTO {

    private String fullName;
    private String passportNumber;
    private String phone;
    private String nationality;
}

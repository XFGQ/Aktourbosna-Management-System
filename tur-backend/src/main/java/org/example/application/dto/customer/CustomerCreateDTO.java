package org.example.application.dto.customer;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CustomerCreateDTO {

    @NotBlank
    private String fullName;

    private String passportNumber;
    private String phone;
    private String nationality;
}

package org.example.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Customer {
    private Long id;
    private String fullName;
    private String passportNumber;
    private String phone;
    private String nationality;
}

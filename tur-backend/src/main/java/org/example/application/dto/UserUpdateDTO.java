package org.example.application.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UserUpdateDTO {

    @Email
    private String email;

    private String password;
    private String role;
}

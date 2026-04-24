package org.example.application.dto;

import lombok.Data;
import org.example.model.UserRole;

@Data
public class UserRequest {
    private String username;
    private String email;
    private String password;
    private UserRole role;
}

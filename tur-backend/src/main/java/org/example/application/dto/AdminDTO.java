package org.example.application.dto;

import lombok.Data;
import org.example.model.UserRole;

@Data
public class AdminDTO {
    // Admin identity
    private Long id;

    // Linked user fields
    private Long userId;
    private String username;
    private String email;
    private UserRole role;

    // Admin-specific field
    private String department;
}

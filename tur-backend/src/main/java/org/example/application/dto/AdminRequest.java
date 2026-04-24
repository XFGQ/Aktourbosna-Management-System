package org.example.application.dto;

import lombok.Data;

@Data
public class AdminRequest {
    // Which user this admin profile belongs to
    private Long userId;

    // Admin-specific field (no auth fields — those live on User)
    private String department;
}

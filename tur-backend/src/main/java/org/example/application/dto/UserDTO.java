package org.example.application.dto;

import lombok.Data;
import org.example.model.UserRole;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private UserRole role;
    private Long guideId;
    private Long adminId;
}

package org.example.application.dto;

import lombok.Data;
import org.example.model.UserRole;

import java.util.List;

@Data
public class GuideDTO {
    // Guide identity
    private Long id;

    // Linked user fields
    private Long userId;
    private String username;
    private String email;
    private UserRole role;

    // Guide-specific fields
    private String fullName;
    private String phone;
    private String jobTitle;
    private String partnerCode;
    private String baseCity;
    private String licenseNo;
    private Double dailyFee;
    private Integer experience;
    private Double rating;
    private String currency;
    private List<String> languages;
    private List<String> countries;
    private List<String> skills;
}

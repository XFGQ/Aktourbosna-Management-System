package org.example.application.dto;

import lombok.Data;

import java.util.List;

@Data
public class GuideRequest {
    // Which user this guide profile belongs to
    private Long userId;

    // Guide-specific fields (no auth fields — those live on User)
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

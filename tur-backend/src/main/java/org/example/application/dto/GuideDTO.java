package org.example.application.dto;

import lombok.Data;

@Data
public class GuideDTO {
    private Long id;
    private Long userId;
    private String fullName;
    private String phone;
    private String baseCity;
    private String licenseNo;
    private Integer experience;
    private Double dailyFee;
}
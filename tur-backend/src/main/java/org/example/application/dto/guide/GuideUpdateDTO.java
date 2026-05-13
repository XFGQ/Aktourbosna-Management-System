package org.example.application.dto.guide;

import lombok.Data;

import java.util.List;

@Data
public class GuideUpdateDTO {

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

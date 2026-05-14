package org.example.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class Guide {
    private Long id;
    private Long userId;
    private String fullName;
    private String email;
    private String phone;
    private String jobTitle;
    private String partnerCode;
    private String baseCity;
    private String licenseNo;
    private Integer experience;
    private Double dailyFee;
    private Double rating;
    private String currency;
    private List<String> languages;
    private List<String> countries;
    private List<String> skills;
    private int tourCount;
}

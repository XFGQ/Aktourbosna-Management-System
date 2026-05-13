package org.example.application.dto.guide;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class GuideCreateDTO {

    @NotNull
    private Long userId;

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

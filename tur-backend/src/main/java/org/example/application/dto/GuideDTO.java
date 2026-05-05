package org.example.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GuideDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long userId;
    private String fullName;
    private String email;
    private String phone;
    private String baseCity;
    private String licenseNo;
    private Integer experience;
    private Double dailyFee;
}
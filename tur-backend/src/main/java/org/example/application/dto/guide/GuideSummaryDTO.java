package org.example.application.dto.guide;

import lombok.Data;

import java.util.List;

@Data
public class GuideSummaryDTO {

    private Long id;
    private String username;
    private String baseCity;
    private Integer experience;
    private Double rating;
    private String currency;
    private List<String> languages;
    private List<String> countries;
    private List<String> skills;
}

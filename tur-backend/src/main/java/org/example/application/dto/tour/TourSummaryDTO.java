package org.example.application.dto.tour;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TourSummaryDTO {

    private Long tourId;
    private String tourName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long guideId;
    private String guidePartnerCode;
    private int customerCount;
}

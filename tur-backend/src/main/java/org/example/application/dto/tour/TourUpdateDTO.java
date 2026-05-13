package org.example.application.dto.tour;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class TourUpdateDTO {

    private String tourName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String hotelName;
    private Double calculatedPrice;
    private Double finalPrice;
    private Long guideId;
    private Long vehicleId;
    private Long routeId;
    private List<Long> extraWaypointIds;
}

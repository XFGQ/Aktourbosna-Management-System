package org.example.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class TourDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long tourId;

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

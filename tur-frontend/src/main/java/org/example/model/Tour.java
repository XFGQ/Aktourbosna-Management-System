package org.example.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
public class Tour {
    private Long tourId;
    private String tourName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String hotelName;
    private Double calculatedPrice = 0.0;
    private Double finalPrice = 0.0;
    private Long guideId;
    private Long vehicleId;
    private Long routeId;
    private List<Long> extraWaypointIds;
}

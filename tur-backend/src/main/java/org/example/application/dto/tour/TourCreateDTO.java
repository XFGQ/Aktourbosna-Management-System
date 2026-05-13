package org.example.application.dto.tour;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class TourCreateDTO {

    @NotBlank
    private String tourName;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    private String hotelName;
    private Double calculatedPrice;
    private Double finalPrice;

    @NotNull
    private Long guideId;

    private Long vehicleId;
    private Long routeId;
    private List<Long> extraWaypointIds;
}

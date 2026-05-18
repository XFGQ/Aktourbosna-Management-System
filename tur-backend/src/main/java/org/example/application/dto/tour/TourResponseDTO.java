package org.example.application.dto.tour;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class TourResponseDTO {

    private Long tourId;
    private String tourName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String hotelName;
    private Double calculatedPrice;
    private Double finalPrice;

    private Long guideId;
    private String guideUsername;

    private Long vehicleId;
    private String vehiclePlate;

    private Long routeId;
    private String routeName;

    private List<Long> extraWaypointIds;

    private int customerCount;
    private Double totalExpense;
}

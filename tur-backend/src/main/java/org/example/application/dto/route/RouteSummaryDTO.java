package org.example.application.dto.route;

import lombok.Data;

@Data
public class RouteSummaryDTO {

    private Long routeId;
    private String routeName;
    private String startCity;
    private String endCity;
    private String country;
    private Float distance;
}

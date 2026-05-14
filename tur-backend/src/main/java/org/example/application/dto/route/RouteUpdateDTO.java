package org.example.application.dto.route;

import lombok.Data;

import java.util.List;

@Data
public class RouteUpdateDTO {

    private String routeName;
    private String startCity;
    private String endCity;
    private String country;
    private Float distance;
    private Double basePrice;
    private List<Long> waypointIds;
    private List<Long> tollIds;
}

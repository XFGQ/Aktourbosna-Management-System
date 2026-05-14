package org.example.application.dto.route;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class RouteCreateDTO {

    @NotBlank
    private String routeName;

    @NotBlank
    private String startCity;

    @NotBlank
    private String endCity;

    private String country;
    private Float distance;
    private Double basePrice;
    private List<Long> waypointIds;
    private List<Long> tollIds;
}

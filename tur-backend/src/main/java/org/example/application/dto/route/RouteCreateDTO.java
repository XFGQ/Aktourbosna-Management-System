package org.example.application.dto.route;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.example.application.dto.TollDTO;
import org.example.application.dto.WaypointDTO;

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
    private List<WaypointDTO> defaultWaypoints;
    private List<TollDTO> tolls;
}

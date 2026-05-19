package org.example.application.dto.route;

import lombok.Data;
import org.example.application.dto.TollDTO;
import org.example.application.dto.WaypointDTO;

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
    private List<WaypointDTO> defaultWaypoints;
    private List<TollDTO> tolls;
}

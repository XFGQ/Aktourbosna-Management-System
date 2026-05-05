package org.example.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long routeId;
    private String routeName;
    private String startCity;
    private String endCity;
    private String country;
    private Float distance;
    private Double basePrice;

    private List<WaypointDTO> defaultWaypoints;
    private List<TollDTO> tolls;
}

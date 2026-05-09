package org.example.application.mapper;

import org.example.application.dto.TourDTO;
import org.example.model.Tour;
import org.example.model.Waypoint;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TourMapper {

    @Mapping(source = "guide.id", target = "guideId")
    @Mapping(source = "vehicle.id", target = "vehicleId")
    @Mapping(source = "baseRoute.routeId", target = "routeId")
    @Mapping(source = "extraWaypoints", target = "extraWaypointIds")
    TourDTO toDto(Tour tour);

    @Mapping(source = "guideId", target = "guide.id")
    @Mapping(source = "vehicleId", target = "vehicle.id")
    @Mapping(source = "routeId", target = "baseRoute.routeId")
    @Mapping(target = "extraWaypoints", ignore = true)
    @Mapping(target = "customers", ignore = true)
    @Mapping(target = "expenses", ignore = true)
    Tour toEntity(TourDTO dto);

    @Mapping(target = "guide", ignore = true)
    @Mapping(target = "vehicle", ignore = true)
    @Mapping(target = "baseRoute", ignore = true)
    @Mapping(target = "extraWaypoints", ignore = true)
    @Mapping(target = "customers", ignore = true)
    @Mapping(target = "expenses", ignore = true)
    @Mapping(target = "tourId", ignore = true)
    void updateEntityFromDto(TourDTO dto, @MappingTarget Tour entity);

    default Long waypointToId(Waypoint waypoint) {
        return waypoint != null ? waypoint.getId() : null;
    }
}

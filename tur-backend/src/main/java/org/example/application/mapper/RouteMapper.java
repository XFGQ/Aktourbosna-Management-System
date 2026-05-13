package org.example.application.mapper;

import org.example.application.dto.route.RouteCreateDTO;
import org.example.application.dto.route.RouteResponseDTO;
import org.example.application.dto.route.RouteSummaryDTO;
import org.example.application.dto.route.RouteUpdateDTO;
import org.example.model.Route;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {WaypointMapper.class, TollMapper.class})
public interface RouteMapper {

    RouteResponseDTO toResponse(Route route);

    RouteSummaryDTO toSummary(Route route);

    @Mapping(target = "defaultWaypoints", ignore = true)
    @Mapping(target = "tolls", ignore = true)
    Route toEntity(RouteCreateDTO dto);

    @Mapping(target = "routeId", ignore = true)
    @Mapping(target = "defaultWaypoints", ignore = true)
    @Mapping(target = "tolls", ignore = true)
    void updateEntityFromDto(RouteUpdateDTO dto, @MappingTarget Route entity);
}

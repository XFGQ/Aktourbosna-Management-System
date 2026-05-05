package org.example.application.mapper;

import org.example.application.dto.RouteDTO;
import org.example.model.Route;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {WaypointMapper.class, TollMapper.class})
public interface RouteMapper {
    RouteDTO toDto(Route route);
    Route toEntity(RouteDTO routeDTO);
    void updateEntityFromDto(RouteDTO dto, @MappingTarget Route entity);
}
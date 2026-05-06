package org.example.application.mapper;

import org.example.application.dto.WaypointDTO;
import org.example.model.Waypoint;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface WaypointMapper{

    WaypointDTO toDto(Waypoint waypoint);
    Waypoint toEntity(WaypointDTO waypointDTO);
    void updateEntityFromDto(WaypointDTO dto, @MappingTarget Waypoint entity);
}

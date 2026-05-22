package org.example.application.mapper;

import org.example.application.dto.WaypointDTO;
import org.example.model.Waypoint;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface WaypointMapper{

    WaypointDTO toDto(Waypoint waypoint);

    @Mapping(target = "id", ignore = true)
    Waypoint toEntity(WaypointDTO waypointDTO);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(WaypointDTO dto, @MappingTarget Waypoint entity);
}

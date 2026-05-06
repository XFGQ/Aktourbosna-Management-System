package org.example.application.mapper;

import org.example.application.dto.VehicleDTO;
import org.example.model.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget; // Bunu import etmeyi unutma!

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface VehicleMapper {

    VehicleDTO toDto(Vehicle vehicle);

    Vehicle toEntity(VehicleDTO vehicleDTO);

    @Mapping(target = "id", ignore = true)
     void updateEntityFromDto(VehicleDTO dto, @MappingTarget Vehicle entity);
}
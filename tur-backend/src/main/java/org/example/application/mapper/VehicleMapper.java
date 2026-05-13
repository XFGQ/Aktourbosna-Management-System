package org.example.application.mapper;

import org.example.application.dto.vehicle.VehicleCreateDTO;
import org.example.application.dto.vehicle.VehicleResponseDTO;
import org.example.application.dto.vehicle.VehicleSummaryDTO;
import org.example.application.dto.vehicle.VehicleUpdateDTO;
import org.example.model.Vehicle;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface VehicleMapper {

    @Mapping(expression = "java(vehicle.getTours().size())", target = "tourCount")
    VehicleResponseDTO toResponse(Vehicle vehicle);

    VehicleSummaryDTO toSummary(Vehicle vehicle);

    Vehicle toEntity(VehicleCreateDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tours", ignore = true)
    @Mapping(target = "serviceHistory", ignore = true)
    void updateEntityFromDto(VehicleUpdateDTO dto, @MappingTarget Vehicle entity);
}

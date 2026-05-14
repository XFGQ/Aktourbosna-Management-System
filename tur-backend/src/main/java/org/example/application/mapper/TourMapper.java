package org.example.application.mapper;

import org.example.application.dto.tour.TourCreateDTO;
import org.example.application.dto.tour.TourResponseDTO;
import org.example.application.dto.tour.TourSummaryDTO;
import org.example.application.dto.tour.TourUpdateDTO;
import org.example.model.Tour;
import org.example.model.Waypoint;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TourMapper {

    @Mapping(source = "guide.id", target = "guideId")
    @Mapping(source = "guide.partnerCode", target = "guidePartnerCode")
    @Mapping(source = "vehicle.id", target = "vehicleId")
    @Mapping(source = "vehicle.plateNumber", target = "vehiclePlate")
    @Mapping(source = "baseRoute.routeId", target = "routeId")
    @Mapping(source = "baseRoute.routeName", target = "routeName")
    @Mapping(source = "extraWaypoints", target = "extraWaypointIds")
    @Mapping(expression = "java(tour.getCustomers().size())", target = "customerCount")
    @Mapping(expression = "java(tour.getExpenses().stream().mapToDouble(e -> e.getAmount() != null ? e.getAmount() : 0.0).sum())", target = "totalExpense")
    TourResponseDTO toResponse(Tour tour);

    @Mapping(source = "guide.id", target = "guideId")
    @Mapping(source = "guide.partnerCode", target = "guidePartnerCode")
    @Mapping(expression = "java(tour.getCustomers().size())", target = "customerCount")
    TourSummaryDTO toSummary(Tour tour);

    @Mapping(source = "guideId", target = "guide.id")
    @Mapping(source = "vehicleId", target = "vehicle.id")
    @Mapping(source = "routeId", target = "baseRoute.routeId")
    @Mapping(target = "tourId", ignore = true)
    @Mapping(target = "extraWaypoints", ignore = true)
    @Mapping(target = "customers", ignore = true)
    @Mapping(target = "expenses", ignore = true)
    Tour toEntity(TourCreateDTO dto);

    @Mapping(target = "guide", ignore = true)
    @Mapping(target = "vehicle", ignore = true)
    @Mapping(target = "baseRoute", ignore = true)
    @Mapping(target = "extraWaypoints", ignore = true)
    @Mapping(target = "customers", ignore = true)
    @Mapping(target = "expenses", ignore = true)
    @Mapping(target = "tourId", ignore = true)
    void updateEntityFromDto(TourUpdateDTO dto, @MappingTarget Tour entity);

    default Long waypointToId(Waypoint waypoint) {
        return waypoint != null ? waypoint.getId() : null;
    }
}

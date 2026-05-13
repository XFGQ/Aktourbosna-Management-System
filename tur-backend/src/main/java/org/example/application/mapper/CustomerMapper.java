package org.example.application.mapper;

import org.example.application.dto.CustomerDTO;
import org.example.model.Customer;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CustomerMapper {

    @Mapping(source = "tour.tourId", target = "tourId")
    CustomerDTO toDto(Customer customer);

    @Mapping(source = "tourId", target = "tour.tourId")
    Customer toEntity(CustomerDTO dto);

    @Mapping(target = "tour", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(CustomerDTO dto, @MappingTarget Customer entity);
}

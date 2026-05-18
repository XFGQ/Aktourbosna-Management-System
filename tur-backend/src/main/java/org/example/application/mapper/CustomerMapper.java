package org.example.application.mapper;

import org.example.application.dto.customer.CustomerCreateDTO;
import org.example.application.dto.customer.CustomerResponseDTO;
import org.example.application.dto.customer.CustomerUpdateDTO;
import org.example.model.Customer;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CustomerMapper {

    @Mapping(source = "tour.tourId", target = "tourId")
    CustomerResponseDTO toResponse(Customer customer);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tour", ignore = true)
    Customer toEntity(CustomerCreateDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tour", ignore = true)
    void updateEntityFromDto(CustomerUpdateDTO dto, @MappingTarget Customer entity);
}

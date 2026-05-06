package org.example.application.mapper;

import org.example.application.dto.TollDTO;
import org.example.model.Toll;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TollMapper {
    TollDTO toDto(Toll toll);
    Toll toEntity(TollDTO tollDTO);
    void updateEntityFromDto(TollDTO dto, @MappingTarget Toll entity);
}
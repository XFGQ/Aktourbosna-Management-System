package org.example.application.mapper;

import org.example.application.dto.GuideDTO;
import org.example.model.Guide;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface GuideMapper {

     @Mapping(source = "user.username", target = "fullName")
    @Mapping(source = "user.id", target = "userId")
    GuideDTO toDto(Guide guide);

     @Mapping(source = "userId", target = "user.id")
    @Mapping(target = "user.username", ignore = true)
    Guide toEntity(GuideDTO guideDTO);

    // PUT işlemlerinde:
    @Mapping(source = "userId", target = "user.id")
    @Mapping(target = "user.username", ignore = true)
    void updateEntityFromDto(GuideDTO dto, @MappingTarget Guide entity);
}
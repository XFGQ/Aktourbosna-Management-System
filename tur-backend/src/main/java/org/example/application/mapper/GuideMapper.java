package org.example.application.mapper;

import org.example.application.dto.guide.GuideCreateDTO;
import org.example.application.dto.guide.GuideResponseDTO;
import org.example.application.dto.guide.GuideSummaryDTO;
import org.example.application.dto.guide.GuideUpdateDTO;
import org.example.model.Guide;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface GuideMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "fullName")
    @Mapping(source = "user.email", target = "email")
    @Mapping(expression = "java(guide.getTours().size())", target = "tourCount")
    GuideResponseDTO toResponse(Guide guide);

    @Mapping(source = "user.username", target = "fullName")
    GuideSummaryDTO toSummary(Guide guide);

    @Mapping(source = "userId", target = "user.id")
    @Mapping(target = "user.username", ignore = true)
    @Mapping(target = "user.email", ignore = true)
    @Mapping(target = "tours", ignore = true)
    Guide toEntity(GuideCreateDTO dto);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tours", ignore = true)
    void updateEntityFromDto(GuideUpdateDTO dto, @MappingTarget Guide entity);
}

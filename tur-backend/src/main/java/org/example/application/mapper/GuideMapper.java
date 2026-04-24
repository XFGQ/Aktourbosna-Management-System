package org.example.application.mapper;

import org.example.application.dto.GuideDTO;
import org.example.application.dto.GuideRequest;
import org.example.model.Guide;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GuideMapper {

    // Pull user fields up into the flat DTO
    @Mapping(target = "userId",   source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "email",    source = "user.email")
    @Mapping(target = "role",     source = "user.role")
    GuideDTO toDto(Guide guide);

    // Create a bare Guide entity — User link is handled in GuideService
    @BeanMapping(builder = @Builder(disableBuilder = true))
    @Mapping(target = "user", ignore = true)
    Guide toEntity(GuideRequest request);

    // Update only guide-specific fields; never touch the user link
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "user", ignore = true)
    void updateEntity(GuideRequest request, @MappingTarget Guide target);
}

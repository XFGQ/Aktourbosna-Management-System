package org.example.application.mapper;

import org.example.application.dto.UserDTO;
import org.example.application.dto.UserRequest;
import org.example.model.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "guideId", source = "guide.id")
    @Mapping(target = "adminId", source = "admin.id")
    UserDTO toDto(User user);

    @BeanMapping(builder = @Builder(disableBuilder = true))
    @Mapping(target = "guide", ignore = true)
    @Mapping(target = "admin", ignore = true)
    User toEntity(UserRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "guide", ignore = true)
    @Mapping(target = "admin", ignore = true)
    void updateEntity(UserRequest request, @MappingTarget User target);
}

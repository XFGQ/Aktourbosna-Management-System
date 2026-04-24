package org.example.application.mapper;

import org.example.application.dto.AdminDTO;
import org.example.application.dto.AdminRequest;
import org.example.model.Admin;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AdminMapper {

    // Pull user fields up into the flat DTO
    @Mapping(target = "userId",   source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "email",    source = "user.email")
    @Mapping(target = "role",     source = "user.role")
    AdminDTO toDto(Admin admin);

    // Create a bare Admin entity — User link is handled in AdminService
    @BeanMapping(builder = @Builder(disableBuilder = true))
    @Mapping(target = "user", ignore = true)
    Admin toEntity(AdminRequest request);

    // Update only admin-specific fields; never touch the user link
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "user", ignore = true)
    void updateEntity(AdminRequest request, @MappingTarget Admin target);
}

package com.spx.auth_service.mappers;

import com.spx.auth_service.dto.AuthRequestDTO;
import com.spx.auth_service.dto.UserResponseDTO;
import com.spx.auth_service.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface UserMapper {

    // ==========================================================
    // DTO -> Entity
    // ==========================================================

    // Database decides the id
    @Mapping(target = "id", ignore = true)

    // Roles are assigned by the service layer
    @Mapping(target = "roles", ignore = true)
    User toUser(AuthRequestDTO authRequestDTO);


    // ==========================================================
    // Entity -> DTO
    // ==========================================================

    UserResponseDTO toDto(User user);
}

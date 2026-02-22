package io.hexlet.spring.mapper;

import io.hexlet.spring.dto.UserCreateDTO;
import io.hexlet.spring.dto.UserDTO;
import io.hexlet.spring.dto.UserPatchDTO;
import io.hexlet.spring.dto.UserUpdateDTO;
import io.hexlet.spring.model.User;
import org.mapstruct.*;

@Mapper(
        uses = { JsonNullableMapper.class },
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {
    UserDTO toDTO(User user);
    User toEntity(UserCreateDTO dto);
    void updateEntity(UserUpdateDTO dto, @MappingTarget User user);
    void updateEntityFromPatch(UserPatchDTO dto, @MappingTarget User user);
}

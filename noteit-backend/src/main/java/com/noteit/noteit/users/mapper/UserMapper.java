package com.noteit.noteit.users.mapper;

import com.noteit.noteit.users.dtos.UserDto;
import com.noteit.noteit.users.model.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserEntity toEntity(UserDto userDto);
    UserDto toDto(UserEntity userEntity);
}

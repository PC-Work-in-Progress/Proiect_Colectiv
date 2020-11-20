package com.noteit.noteit.helper.mapper;

import com.noteit.noteit.dtos.UserDto;
import com.noteit.noteit.entities.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserEntity toEntity(UserDto userDto);
    UserDto toDto(UserEntity userEntity);
}

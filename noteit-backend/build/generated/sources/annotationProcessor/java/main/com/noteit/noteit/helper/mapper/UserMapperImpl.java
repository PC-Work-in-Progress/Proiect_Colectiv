package com.noteit.noteit.helper.mapper;

import com.noteit.noteit.dtos.UserDto;
import com.noteit.noteit.entities.UserEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
<<<<<<< HEAD
    date = "2020-11-20T23:55:38+0200",
    comments = "version: 1.4.1.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-6.6.1.jar, environment: Java 11.0.9.1 (Amazon.com Inc.)"
=======
    date = "2020-11-21T17:12:13+0200",
    comments = "version: 1.4.1.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-6.6.1.jar, environment: Java 11.0.9 (Amazon.com Inc.)"
>>>>>>> main
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserEntity toEntity(UserDto userDto) {
        if ( userDto == null ) {
            return null;
        }

        UserEntity userEntity = new UserEntity();

        userEntity.setEmail( userDto.getEmail() );
        userEntity.setFull_name( userDto.getFull_name() );
        userEntity.setUsername( userDto.getUsername() );

        return userEntity;
    }

    @Override
    public UserDto toDto(UserEntity userEntity) {
        if ( userEntity == null ) {
            return null;
        }

        UserDto userDto = new UserDto();

        userDto.setEmail( userEntity.getEmail() );
        userDto.setFull_name( userEntity.getFull_name() );
        userDto.setUsername( userEntity.getUsername() );

        return userDto;
    }
}

package com.noteit.noteit.helper.mapper;

import com.noteit.noteit.dtos.UserDto;
import com.noteit.noteit.entities.UserEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
<<<<<<< HEAD
    date = "2020-11-20T18:07:31+0200",
    comments = "version: 1.4.1.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-6.6.1.jar, environment: Java 11.0.1 (Oracle Corporation)"
=======
    date = "2020-11-21T11:13:39+0200",
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

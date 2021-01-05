package com.noteit.noteit.services;

import com.noteit.noteit.dtos.UserDto;
import com.noteit.noteit.entities.UserEntity;
import com.noteit.noteit.helper.mapper.UserMapper;
import com.noteit.noteit.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.hibernate.service.spi.ServiceException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImplementation implements UserServiceInterface {
    private UserRepository userRepository;
    private UserMapper userMapper;

    /**
     * Get a userDTO by a given token
     * @param token - String
     * @return userDTO
     */
    @Override
    public UserDto getUserByToken(String token) {
        UserEntity userEntity = userRepository.findByToken(token);

        if(userEntity == null) {
            throw new ServiceException("No user with this token");
        }

        return userMapper.toDto(userEntity);
    }

    @Override
    public String getUserIdByToken(String token) {
        return userRepository.findByToken(token).getId();
    }

    @Override
    public String getUsernameById(String id) {
        return userRepository.findById(id).get().getUsername();
    }
}

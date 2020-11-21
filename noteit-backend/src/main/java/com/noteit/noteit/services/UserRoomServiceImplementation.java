package com.noteit.noteit.services;

import com.noteit.noteit.entities.UserRoomEntity;
import com.noteit.noteit.repositories.UserRoomRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserRoomServiceImplementation implements UserRoomServiceInterface {
    private UserRoomRepository userRoomRepository;

    @Override
    public List<UserRoomEntity> getUsersRooms() {
        return userRoomRepository.findAll();
    }
}

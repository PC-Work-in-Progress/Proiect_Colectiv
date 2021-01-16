package com.noteit.noteit.rooms.services;

import com.noteit.noteit.rooms.model.UserRoomEntity;
import com.noteit.noteit.rooms.repository.UserRoomRepository;
import com.noteit.noteit.rooms.services.UserRoomServiceInterface;
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

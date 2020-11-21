package com.noteit.noteit.services;

import com.noteit.noteit.dtos.RoomDto;
import com.noteit.noteit.entities.RoomEntity;
import com.noteit.noteit.entities.UserEntity;
import com.noteit.noteit.entities.UserRoomEntity;
import com.noteit.noteit.repositories.RoomRepository;
import com.noteit.noteit.repositories.UserRepository;
import com.noteit.noteit.repositories.UserRoomRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RoomServiceImplementation implements RoomServiceInterface {
    private RoomRepository roomRepository;
    private UserRepository userRepository;
    private UserRoomRepository userRoomRepository;

    @Override
    public List<RoomDto> getRooms() {
        List<RoomDto> roomDtos = new ArrayList<>();
        for (RoomEntity roomEntity : roomRepository.findAll()) {
            RoomDto roomDto = new RoomDto(roomEntity.getId(), roomEntity.getName());
            roomDtos.add(roomDto);
        }

        return roomDtos;
    }

    @Override
    public List<RoomDto> getRoomsByToken(String token) {
        List<RoomDto> roomDtos = new ArrayList<>();
        String ownerId = userRepository.findByToken(token).getId();
        Optional<List<RoomEntity>> roomEntities = roomRepository.findByOwnerId(ownerId);
        if (!roomEntities.isEmpty())
        {
            for (RoomEntity roomEntity : roomEntities.get()) {
                RoomDto roomDto = new RoomDto(roomEntity.getId(), roomEntity.getName());
                roomDtos.add(roomDto);
            }
        }
        return roomDtos;
    }

    @Override
    public void createRoom(String name, String token) {
        String ownerId = userRepository.findByToken(token).getId();
        RoomEntity roomEntity = new RoomEntity(name, ownerId);
        roomRepository.save(roomEntity);
        UserRoomEntity userRoom = new UserRoomEntity(ownerId, roomEntity.getId());
        userRoomRepository.save(userRoom);
    }
}

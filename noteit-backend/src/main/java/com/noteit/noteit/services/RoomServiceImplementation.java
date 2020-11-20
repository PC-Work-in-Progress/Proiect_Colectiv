package com.noteit.noteit.services;

import com.noteit.noteit.entities.RoomEntity;
import com.noteit.noteit.repositories.RoomRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class RoomServiceImplementation implements RoomServiceInterface {
    private RoomRepository roomRepository;

    @Override
    public List<RoomEntity> getRooms() {
        return roomRepository.findAll();
    }

    @Override
    public List<RoomEntity> getRoomsByOwnerId(String ownerId) {
        return roomRepository.findByOwnerId(ownerId).get();
    }

    @Override
    public void createRoom(String name, String ownerId) {

        /** TODO:
         * Validare pentru 'nume' ???
        */

        RoomEntity roomEntity = new RoomEntity(name, ownerId);
        roomRepository.save(roomEntity);
    }
}

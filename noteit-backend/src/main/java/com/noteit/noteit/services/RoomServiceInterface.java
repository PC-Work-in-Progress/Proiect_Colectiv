package com.noteit.noteit.services;

import com.noteit.noteit.entities.RoomEntity;

import java.util.List;

public interface RoomServiceInterface {

    List<RoomEntity> getRoomsByOwnerId(String ownerId);

    List<RoomEntity> getRooms();

    void createRoom(String name, String ownerId);

}

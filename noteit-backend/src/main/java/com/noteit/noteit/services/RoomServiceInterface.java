package com.noteit.noteit.services;

import com.noteit.noteit.dtos.RoomDto;
import com.noteit.noteit.entities.RoomEntity;

import java.util.List;

public interface RoomServiceInterface {

    List<RoomDto> getRoomsByToken(String token);

    List<RoomDto> getRooms();

    void createRoom(String name, String token);

}

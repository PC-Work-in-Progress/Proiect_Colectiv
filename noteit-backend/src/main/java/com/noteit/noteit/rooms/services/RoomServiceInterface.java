package com.noteit.noteit.rooms.services;

import com.noteit.noteit.rooms.dtos.RoomDto;
import com.noteit.noteit.rooms.model.RoomEntity;

import java.util.List;

public interface RoomServiceInterface {

    List<RoomDto> getRoomsByToken(String token);

    List<RoomDto> getRooms();

    RoomDto createRoom(String name, String token);

    RoomEntity getById(String id);

    List<RoomEntity> getByName(String name);

    List<RoomEntity> filterRooms(List<String> tagNames);

    String checkIfIsAdmin(String token, String roomId);

    void joinRoom(String token, String roomId);
}

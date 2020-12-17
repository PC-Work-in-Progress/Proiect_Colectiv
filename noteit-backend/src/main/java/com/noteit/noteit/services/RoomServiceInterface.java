package com.noteit.noteit.services;

import com.noteit.noteit.dtos.RoomDto;
import com.noteit.noteit.entities.RoomEntity;
import com.noteit.noteit.entities.TagEntity;

import javax.swing.text.html.HTML;
import java.util.List;

public interface RoomServiceInterface {

    List<RoomDto> getRoomsByToken(String token);

    List<RoomDto> getRooms();

    RoomDto createRoom(String name, String token);

    RoomEntity getById(String id);

    List<RoomEntity> getByName(String name);

    List<RoomEntity> filterRooms(List<String> tagNames);

    String checkIfIsAdmin(String token, String roomId);
}

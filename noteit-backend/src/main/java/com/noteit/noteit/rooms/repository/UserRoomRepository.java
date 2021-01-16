package com.noteit.noteit.rooms.repository;

import com.noteit.noteit.rooms.model.UserRoomEntity;
import com.noteit.noteit.rooms.model.UserRoomIdEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRoomRepository  extends JpaRepository<UserRoomEntity, UserRoomIdEntity<String, String>> {
    List<UserRoomEntity> findUserRoomEntityByUserRoomId_UserId(String user_id);
    UserRoomEntity findUserRoomEntityByUserRoomId_RoomId(String room_id);
}

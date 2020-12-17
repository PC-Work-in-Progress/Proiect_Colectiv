package com.noteit.noteit.repositories;

import com.noteit.noteit.entities.UserRoomEntity;
import com.noteit.noteit.entities.UserRoomIdEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRoomRepository  extends JpaRepository<UserRoomEntity, UserRoomIdEntity<String, String>> {
    List<UserRoomEntity> findUserRoomEntityByUserRoomId_UserId(String user_id);
    UserRoomEntity findUserRoomEntityByUserRoomId_RoomId(String room_id);
}

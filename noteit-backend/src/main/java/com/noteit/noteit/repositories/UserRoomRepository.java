package com.noteit.noteit.repositories;

import com.noteit.noteit.entities.UserRoomEntity;
import com.noteit.noteit.entities.UserRoomIdEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoomRepository  extends JpaRepository<UserRoomEntity, UserRoomIdEntity<String, String>> {
}

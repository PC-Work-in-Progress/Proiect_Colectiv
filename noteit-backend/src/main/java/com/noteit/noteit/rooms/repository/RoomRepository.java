package com.noteit.noteit.rooms.repository;

import com.noteit.noteit.rooms.model.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity, String> {
    Optional<List<RoomEntity>> findByOwnerId(String ownerId);
    Optional<RoomEntity> findById(String id);
    Optional<List<RoomEntity>> findByNameContains(String name);
}

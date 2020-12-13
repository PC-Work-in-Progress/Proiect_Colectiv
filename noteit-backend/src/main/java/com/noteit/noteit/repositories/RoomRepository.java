package com.noteit.noteit.repositories;

import com.noteit.noteit.entities.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity, String> {
    Optional<List<RoomEntity>> findByOwnerId(String ownerId);
    Optional<RoomEntity> findById(String id);
}

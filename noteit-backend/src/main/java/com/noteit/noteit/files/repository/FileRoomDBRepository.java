package com.noteit.noteit.files.repository;

import com.noteit.noteit.files.model.FileRoomCompositePK;
import com.noteit.noteit.files.model.FileRoomDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRoomDBRepository extends JpaRepository<FileRoomDB, FileRoomCompositePK> {
    List<FileRoomDB> findById_RoomId(String roomId);
    List<FileRoomDB> findById_FileIdAndId_RoomId(String fileId, String roomId);
    List<FileRoomDB> findById_FileId(String fileId);
    List<FileRoomDB> findById_UserId(String userId);
}

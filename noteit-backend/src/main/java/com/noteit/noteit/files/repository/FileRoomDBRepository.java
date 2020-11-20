package com.noteit.noteit.files.repository;

import com.noteit.noteit.files.model.FileRoomCompositePK;
import com.noteit.noteit.files.model.FileRoomDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRoomDBRepository extends JpaRepository<FileRoomDB, FileRoomCompositePK> {
    List<FileRoomDB> findById_RoomId(String fileId);
    List<FileRoomDB> findById_FileIdAndId_RoomId(String fileId, String roomId);
       //List<FileRoomDB> findByFile_id(String fileId);
//    Stream<FileRoomDB> findByRoomId(String roomId);
}

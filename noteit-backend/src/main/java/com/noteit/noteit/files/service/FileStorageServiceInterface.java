package com.noteit.noteit.files.service;

import com.noteit.noteit.files.model.FileDB;
import com.noteit.noteit.files.model.FileRoomDB;

import java.util.List;

public interface FileStorageServiceInterface {
    FileDB getById(String id);
    FileDB add(FileDB fileDB);
    FileRoomDB addFileRoom(FileRoomDB fileRoomDB);
    List<FileRoomDB> findByRoomId(String roomId);
}

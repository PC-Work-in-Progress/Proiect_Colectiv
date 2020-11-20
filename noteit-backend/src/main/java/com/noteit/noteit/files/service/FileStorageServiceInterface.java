package com.noteit.noteit.files.service;

import com.noteit.noteit.files.exception.FileException;
import com.noteit.noteit.files.model.FileDB;
import com.noteit.noteit.files.model.FileRoomDB;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

public interface FileStorageServiceInterface {
    FileDB getById(String id);
    FileDB add(FileDB fileDB);
    FileRoomDB addFileRoom(FileRoomDB fileRoomDB);
    List<FileRoomDB> findByRoomId(String roomId);
    FileDB store(MultipartFile file, String userId, String roomId) throws IOException, FileException;
    Stream<FileDB> getFilesForRoom(String roomId);
}

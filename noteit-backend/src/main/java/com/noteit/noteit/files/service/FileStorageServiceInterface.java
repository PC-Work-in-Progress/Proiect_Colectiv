package com.noteit.noteit.files.service;

import com.noteit.noteit.files.dtos.FileDbDto;
import com.noteit.noteit.files.exception.FileException;
import com.noteit.noteit.files.model.FileDB;
import com.noteit.noteit.files.model.FileRoomDB;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.stream.Stream;

public interface FileStorageServiceInterface {
    FileDB getById(String id);
    FileDB store(MultipartFile file, String userId, String roomId, String tags) throws IOException, FileException;
    Stream<FileDB> getFilesForRoom(String roomId);
    FileDbDto getDetails(String id);
    Stream<FileDB> getNotAcceptedFiles();
    FileDB acceptFile(String fileId);
    FileDB denyFile(String fileId);
}

package com.noteit.noteit.files.service;

import com.noteit.noteit.files.dtos.FileDbDto;
import com.noteit.noteit.files.dtos.FileDbWrapper;
import com.noteit.noteit.files.dtos.FileRoomDto;
import com.noteit.noteit.files.exception.FileException;
import com.noteit.noteit.files.model.FileDB;
import com.noteit.noteit.files.model.FileRoomDB;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface FileStorageServiceInterface {
    FileDB getById(String id);
    FileDB store(MultipartFile file, String userId, String roomId, String tags) throws IOException, FileException;
    List<FileRoomDto> getRecentFilesFromToken(String token, int pageNumber);
    List<FileRoomDto> getSearchedFilesFromName(String token, String filename);
    List<FileRoomDto> getSearchedFilesFromTag(String token, String tag);

    FileDbDto getDetails(String id, String idRoom) throws Exception;

    Optional<FileDB> getFile(String id);
    void acceptFile(String fileId, String roomId) throws FileException;
    FileDB denyFile(String fileId ,String roomId);
    String detectHandwriting(MultipartFile file, String userId) throws IOException;

    Stream<FileDbWrapper> getWrappedFilesForRoom(String roomId);
    void fileViewed(String fileId, String roomId);

    String getUserIdByFileAndRoom(String fileId, String roomId);

    Integer getUserViewsAndDownloadsCount(String userId);
}
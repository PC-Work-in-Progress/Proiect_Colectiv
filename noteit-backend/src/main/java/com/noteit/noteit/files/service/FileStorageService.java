package com.noteit.noteit.files.service;

import com.noteit.noteit.files.model.FileDB;
import com.noteit.noteit.files.model.FileRoomDB;
import com.noteit.noteit.files.repository.FileDBRepository;
import com.noteit.noteit.files.repository.FileRoomDBRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class FileStorageService implements FileStorageServiceInterface{
    @Autowired
    private FileDBRepository fileDBRepository;

    @Autowired
    private FileRoomDBRepository fileRoomDBRepository;

    @Override
    public FileDB getById(String id) {
        return fileDBRepository.findById(id).get();
    }

    @Override
    public FileDB add(FileDB fileDB) {
        return fileDBRepository.save(fileDB);
    }

    @Override
    public FileRoomDB addFileRoom(FileRoomDB fileRoomDB) {
        return fileRoomDBRepository.save(fileRoomDB);
    }

    @Override
    public List<FileRoomDB> findByRoomId(String roomId) {
        return fileRoomDBRepository.findById_RoomId(roomId);
    }
}

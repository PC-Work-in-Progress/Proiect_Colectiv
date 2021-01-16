package com.noteit.noteit.files.mapper;

import com.noteit.noteit.files.dtos.FileDbDto;
import com.noteit.noteit.files.model.FileDB;

import java.util.List;


public class FileDbMapper {
    //    FileDB toEntity(FileDbDto dto);
    public FileDbDto toDto(FileDB entity, String username, List<String> tags) {
        FileDbDto dto = new FileDbDto();
        dto.setUsername(username);
        dto.setDate(entity.getDate());
        dto.setName(entity.getName());
        dto.setSize(entity.getSize());
        dto.setType(entity.getType());
        dto.setTags(tags);
        return dto;
    }
}
package com.noteit.noteit.files.mapper;

import com.noteit.noteit.dtos.UserDto;
import com.noteit.noteit.entities.UserEntity;
import com.noteit.noteit.files.dtos.FileDbDto;
import com.noteit.noteit.files.model.FileDB;
import com.noteit.noteit.helper.mapper.UserMapper;
import com.noteit.noteit.repositories.UserRepository;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

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
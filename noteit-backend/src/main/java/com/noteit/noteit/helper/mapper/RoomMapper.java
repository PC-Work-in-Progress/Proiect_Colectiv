package com.noteit.noteit.helper.mapper;

import com.noteit.noteit.dtos.RoomDto;
import com.noteit.noteit.entities.RoomEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoomMapper {
    RoomEntity toEntity(RoomDto roomDto);
    RoomDto toDto(RoomEntity roomEntity);
}

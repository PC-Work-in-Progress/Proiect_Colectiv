package com.noteit.noteit.rooms.mapper;

import com.noteit.noteit.rooms.dtos.RoomDto;
import com.noteit.noteit.rooms.model.RoomEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoomMapper {
    RoomEntity toEntity(RoomDto roomDto);
    RoomDto toDto(RoomEntity roomEntity);
}

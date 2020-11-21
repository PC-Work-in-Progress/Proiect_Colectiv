package com.noteit.noteit.dtos;

import lombok.Data;

@Data
public class RoomDto {
    private String name;

    public RoomDto()
    {

    }

    public RoomDto(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

package com.noteit.noteit.dtos;

import lombok.Data;

@Data
public class RoomDto {
    private String name;
    private String id;

    public RoomDto()
    {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public RoomDto(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

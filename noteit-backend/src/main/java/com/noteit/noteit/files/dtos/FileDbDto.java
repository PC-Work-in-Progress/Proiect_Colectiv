package com.noteit.noteit.files.dtos;


import com.noteit.noteit.dtos.UserDto;
import com.noteit.noteit.repositories.UserRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/*
DTO for File details containing the file's name,
    its type, the date it was uploaded, the username of the owner, size
    and all the tags related to it
 */
public class FileDbDto {
    private String name;
    private String type;
    private String date;
    private String username;
    private Long size;
    private List<String> tags;

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }
}

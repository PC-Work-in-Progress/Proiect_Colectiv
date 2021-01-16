package com.noteit.noteit.users.dtos;

import lombok.Data;

import javax.persistence.Column;

@Data
public class UserDto {
    private String email;
    private String full_name;
    private String username;
}

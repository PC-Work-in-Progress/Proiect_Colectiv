package com.noteit.noteit.users.services;

import com.noteit.noteit.users.dtos.UserDto;

public interface UserServiceInterface {
    UserDto getUserByToken(String token);
    String getUserIdByToken(String token);
    String getUsernameById(String id);
}

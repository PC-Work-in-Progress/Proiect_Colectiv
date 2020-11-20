package com.noteit.noteit.services;

import com.noteit.noteit.dtos.UserDto;

public interface UserServiceInterface {
    UserDto getUserByToken(String token);
    String getUserIdByToken(String token);
}

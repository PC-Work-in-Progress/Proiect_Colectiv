package com.noteit.noteit.users.controller;

import com.noteit.noteit.rooms.services.UserRoomServiceInterface;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users_rooms")
@CrossOrigin
@AllArgsConstructor
public class UserRoomController {
    UserRoomServiceInterface userRoomService;

    @GetMapping("/")
    public ResponseEntity<?> getUsersRooms(){
        return ResponseEntity.ok().body(userRoomService.getUsersRooms());
    }
}

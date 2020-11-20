package com.noteit.noteit.controller;

import com.noteit.noteit.services.RoomServiceInterface;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rooms")
@CrossOrigin
@AllArgsConstructor
public class RoomController {
    RoomServiceInterface roomService;

    @GetMapping("/")
    public ResponseEntity<?> getRooms(){
        return ResponseEntity.ok().body(roomService.getRooms());
    }

    @GetMapping("/{ownerId}")
    public ResponseEntity<?> getRoomByOwnerId(@PathVariable String ownerId){
        return ResponseEntity.ok().body(roomService.getRoomsByOwnerId(ownerId));
    }

    @PostMapping("/{name}/{ownerId}")
    public void createRoom(@PathVariable String name, @PathVariable String ownerId)
    {
        roomService.createRoom(name, ownerId);
    }
}

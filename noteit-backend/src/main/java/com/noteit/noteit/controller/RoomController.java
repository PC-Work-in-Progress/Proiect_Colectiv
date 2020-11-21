package com.noteit.noteit.controller;

import com.noteit.noteit.services.RoomServiceInterface;
import lombok.AllArgsConstructor;
import org.hibernate.service.spi.ServiceException;
import org.springframework.http.HttpStatus;
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
        try
        {
            return ResponseEntity.ok().body(roomService.getRooms());
        }
        catch (ServiceException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{token}")
    public ResponseEntity<?> getRoomByToken(@PathVariable String token){
        try{
            return ResponseEntity.ok().body(roomService.getRoomsByToken(token));
        }
        catch (ServiceException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{name}/{token}")
    public ResponseEntity<?> createRoom(@PathVariable String name, @PathVariable String token)
    {
        try{
            roomService.createRoom(name, token);
            return ResponseEntity.ok().body(HttpStatus.OK);
        }
        catch (ServiceException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

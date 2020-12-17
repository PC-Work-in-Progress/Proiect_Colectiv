package com.noteit.noteit.controller;

import com.noteit.noteit.dtos.RoomDto;
import com.noteit.noteit.services.RoomServiceInterface;
import lombok.AllArgsConstructor;
import org.hibernate.service.spi.ServiceException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

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

    @GetMapping("/roomsUser")
    public ResponseEntity<?> getRoomByToken(HttpServletRequest request){
        try {
            String header = request.getHeader("Authorization");

            if (header == null || !header.startsWith("Bearer ")) {
                throw new ServiceException("No JWT token found in request headers");
            }

            String authToken = header.split(" ")[1];
            return ResponseEntity.ok().body(roomService.getRoomsByToken(authToken));
        }
        catch (ServiceException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/createRoom")
    public ResponseEntity<?> createRoom(HttpServletRequest request, @RequestBody RoomDto roomDto)
    {
        String name = roomDto.getName();

        try {
            String header = request.getHeader("Authorization");

            if (header == null || !header.startsWith("Bearer ")) {
                throw new ServiceException("No JWT token found in request headers");
            }

            String authToken = header.split(" ")[1];
            return ResponseEntity.ok().body(roomService.createRoom(name, authToken));
        }
        catch (ServiceException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{name}")
    public ResponseEntity<?> getSearchedRoomsByName(@PathVariable String name){
        return ResponseEntity.ok().body(roomService.getByName(name));
    }

    @GetMapping("/filterRooms/{filterTags}")
    public ResponseEntity<?> getFilteredRoomsByFileTags(@PathVariable String filterTags){
        String[] tags=filterTags.split(",");
        return ResponseEntity.ok().body(roomService.filterRooms(Arrays.asList(tags)));
    }
}

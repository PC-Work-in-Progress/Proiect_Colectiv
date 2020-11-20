package com.noteit.noteit.controller;

import com.noteit.noteit.services.UserServiceInterface;
import lombok.AllArgsConstructor;
import org.hibernate.service.spi.ServiceException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@CrossOrigin
@AllArgsConstructor
public class UserController {
    private UserServiceInterface userService;

    @GetMapping("/details/{token}")
    public ResponseEntity<?> getDetailsUser(@PathVariable String token) {
        try {
            return ResponseEntity.ok().body(userService.getUserByToken(token));
        } catch (ServiceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

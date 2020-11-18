package com.noteit.noteit.controller;

import com.noteit.noteit.services.TestServiceInterface;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
@CrossOrigin
@AllArgsConstructor
public class TestController {
    private TestServiceInterface service;

    @GetMapping("/da")
    public ResponseEntity<?> justATest(){
            return ResponseEntity.ok().body("Merge");
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username){
        return ResponseEntity.ok().body(service.getByUsername(username));
    }
}

package com.noteit.noteit.controller;

import com.noteit.noteit.entities.UserEntity;
import com.noteit.noteit.files.message.ResponseMessage;
import com.noteit.noteit.payload.ApiResponse;
import com.noteit.noteit.payload.SignUpRequest;
import com.noteit.noteit.payload.UpdateRequest;
import com.noteit.noteit.repositories.UserRepository;
import com.noteit.noteit.services.UserServiceInterface;
import lombok.AllArgsConstructor;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/user")
@CrossOrigin
@AllArgsConstructor
public class UserController {
    private UserServiceInterface userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @GetMapping("/details")
    public ResponseEntity<?> getDetailsUser(HttpServletRequest request) {
        try {
            String header = request.getHeader("Authorization");

            String authToken = header.split(" ")[1];
            if(authToken == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessage("Unauthorized action!"));
            }

            return ResponseEntity.ok().body(userService.getUserByToken(authToken));
        } catch (ServiceException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PutMapping("/update/{field}")
    public ResponseEntity<?> updateUsername(HttpServletRequest request, @RequestBody UpdateRequest updateRequest, @PathVariable String field) {

        try {
            String header = request.getHeader("Authorization");

            if (header == null || !header.startsWith("Bearer ")) {
                throw new ServiceException("No JWT token found in request headers");
            }

            String authToken = header.split(" ")[1];
            System.out.println("TOKEN");
            System.out.println(authToken);
            System.out.println(updateRequest.getUpdateString());

            if (userRepository.findByToken(authToken) == null) {
                return new ResponseEntity(new ApiResponse(false, "Invalid token"),
                        HttpStatus.OK);
            }

            try {
                switch (field){
                    case "username":
                        userRepository.updateUsername(authToken, updateRequest.getUpdateString());
                        return new ResponseEntity(new ApiResponse(true, "Username updated successfully"),
                                HttpStatus.OK);
                    case "fullname":
                        userRepository.updateFullName(authToken, updateRequest.getUpdateString());
                        return new ResponseEntity(new ApiResponse(true, "Full name updated successfully"),
                                HttpStatus.OK);
                    case "password":
                        String encodedPassword = passwordEncoder.encode(updateRequest.getUpdateString());
                        userRepository.updatePassword(authToken, encodedPassword);
                        return new ResponseEntity(new ApiResponse(true, "Password updated successfully"),
                                HttpStatus.OK);
                    case "email":
                        userRepository.updateEmail(authToken, updateRequest.getUpdateString());
                        return new ResponseEntity(new ApiResponse(true, "Email updated successfully"),
                                HttpStatus.OK);
                    default:
                        return new ResponseEntity(new ApiResponse(false, "Invalid Request"),
                                HttpStatus.BAD_REQUEST);
                }
            } catch (Exception exception) {
                return new ResponseEntity(new ApiResponse(false, "Error updating user"),
                        HttpStatus.OK);
            }
        } catch (Exception exception) {
            return ResponseEntity.badRequest().body(exception.getLocalizedMessage());
        }
    }
}

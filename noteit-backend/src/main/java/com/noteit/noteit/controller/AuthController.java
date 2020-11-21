package com.noteit.noteit.controller;

import com.noteit.noteit.entities.UserEntity;
import com.noteit.noteit.payload.ApiResponse;
import com.noteit.noteit.payload.JwtAuthenticationResponse;
import com.noteit.noteit.payload.LoginRequest;
import com.noteit.noteit.payload.SignUpRequest;
import com.noteit.noteit.repositories.UserRepository;
import com.noteit.noteit.security.JwtTokenProvider;
import com.noteit.noteit.utils.AuthError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenProvider tokenProvider;

    @GetMapping("/test")
    public String test() {
        return "TEST";
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {

//        System.out.println("Username: " + loginRequest.getUsernameOrEmail());
//        System.out.println("Password: " + loginRequest.getPassword());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        System.out.println("***************Generated TOKEN:" + jwt);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignUpRequest signUpRequest) {

        if(userRepository.existsByUsername(signUpRequest.getUsername())) {
            return new ResponseEntity(new ApiResponse(false, "Username is already taken!"),
                    HttpStatus.BAD_REQUEST);
        }

        if(userRepository.existsByEmail(signUpRequest.getEmail())) {
            return new ResponseEntity(new ApiResponse(false, "Email Address already in use!"),
                    HttpStatus.BAD_REQUEST);
        }

        switch (validateInputs(signUpRequest)) {
            case INVALID_NAME:
                return new ResponseEntity(new ApiResponse(false, "Invalid name"),
                    HttpStatus.BAD_REQUEST);
            case INVALID_EMAIL:
                return new ResponseEntity(new ApiResponse(false, "Invalid email"),
                        HttpStatus.BAD_REQUEST);
            case INVALID_USERNAME:
                return new ResponseEntity(new ApiResponse(false, "Invalid username"),
                        HttpStatus.BAD_REQUEST);
            case INVALID_PASSWORD:
                return new ResponseEntity(new ApiResponse(false, "Invalid password"),
                        HttpStatus.BAD_REQUEST);
            // SUCCESS
            default:
                // Creating user's account
                UserEntity user = new UserEntity(String.valueOf(userRepository.getMaxId() + 1), signUpRequest.getEmail(), signUpRequest.getPassword(),
                        signUpRequest.getFull_name(), signUpRequest.getUsername());

                user.setToken(passwordEncoder.encode(user.getToken()));

                UserEntity result = userRepository.save(user);

                URI location = ServletUriComponentsBuilder
                        .fromCurrentContextPath().path("/users/{username}")
                        .buildAndExpand(result.getUsername()).toUri();

                return ResponseEntity.created(location).body(new ApiResponse(true, "User registered successfully"));
        }
    }

    public AuthError validateInputs(SignUpRequest signUpRequest) {
        if(signUpRequest.getFull_name().length() < 2)
            return AuthError.INVALID_NAME;

        if(signUpRequest.getUsername().length() < 2)
            return AuthError.INVALID_USERNAME;

        if(signUpRequest.getPassword().length() < 2)
            return AuthError.INVALID_PASSWORD;

        if(!signUpRequest.getEmail().contains("@") && signUpRequest.getPassword().length() < 4)
            return AuthError.INVALID_EMAIL;

        return AuthError.OK;
    }
}

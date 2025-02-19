package com.noteit.noteit.authentication.controller;

import com.noteit.noteit.users.model.UserEntity;
import com.noteit.noteit.utils.ApiResponse;
import com.noteit.noteit.authentication.message.JwtAuthenticationResponse;
import com.noteit.noteit.authentication.message.LoginRequest;
import com.noteit.noteit.authentication.message.SignUpRequest;
import com.noteit.noteit.users.repository.UserRepository;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@CrossOrigin
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
        userRepository.updateToken(loginRequest.getUsername(), jwt);
        System.out.println("***************Generated TOKEN:" + jwt);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignUpRequest signUpRequest) {

        if(userRepository.existsByUsername(signUpRequest.getUsername())) {
            return new ResponseEntity(new ApiResponse(false, "Username is already taken!"),
                    HttpStatus.OK);
        }

        if(userRepository.existsByEmail(signUpRequest.getEmail())) {
            return new ResponseEntity(new ApiResponse(false, "Email Address already in use!"),
                    HttpStatus.OK);
        }

        switch (validateInputs(signUpRequest)) {
            case INVALID_NAME:
                return new ResponseEntity(new ApiResponse(false, "Invalid name"),
                    HttpStatus.OK);
            case INVALID_EMAIL:
                return new ResponseEntity(new ApiResponse(false, "Invalid email"),
                        HttpStatus.OK);
            case INVALID_USERNAME:
                return new ResponseEntity(new ApiResponse(false, "Invalid username"),
                        HttpStatus.OK);
            case INVALID_PASSWORD:
                return new ResponseEntity(new ApiResponse(false, "Invalid password"),
                        HttpStatus.OK);
            // SUCCESS
            default:
                // Creating user's account

                UserEntity user = new UserEntity(signUpRequest.getEmail(), "token",
                        signUpRequest.getFull_name(), signUpRequest.getUsername(), signUpRequest.getPassword());

                user.setPassword(passwordEncoder.encode(user.getPassword()));


                UserEntity result = userRepository.save(user);
                userRepository.updateToken(user.getUsername(), "token" + user.getId());

                URI location = ServletUriComponentsBuilder
                        .fromCurrentContextPath().path("/users/{username}")
                        .buildAndExpand(result.getUsername()).toUri();

                return ResponseEntity.created(location).body(new ApiResponse(true, "User registered successfully"));
        }
    }

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    // Some validation, needs improvement
    public AuthError validateInputs(SignUpRequest signUpRequest) {
        if(signUpRequest.getFull_name().length() < 2)
            return AuthError.INVALID_NAME;

        if(signUpRequest.getUsername().length() < 2)
            return AuthError.INVALID_USERNAME;

        if(signUpRequest.getPassword().length() < 2)
            return AuthError.INVALID_PASSWORD;

        if(!validate(signUpRequest.getEmail()))
            return AuthError.INVALID_EMAIL;

        return AuthError.OK;
    }
}

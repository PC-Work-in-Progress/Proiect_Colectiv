package com.noteit.noteit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noteit.noteit.NoteitApplicationTests;
import com.noteit.noteit.entities.RoomEntity;
import com.noteit.noteit.payload.LoginRequest;
import com.noteit.noteit.payload.SignUpRequest;
import com.noteit.noteit.repositories.RoomRepository;
import com.noteit.noteit.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.noteit.noteit.entities.UserEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RoomControllerTest extends NoteitApplicationTests {
    @Autowired
    private MockMvc mock;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoomRepository roomRepository;


    private UserEntity userEntity;
    private RoomEntity roomEntity;
    private ObjectMapper mapper = new ObjectMapper();

    public void set_Up() {
        var user = new UserEntity();
        user.setUsername("test");
        user.setPassword("testParola");
        user.setFull_name("test test");
        user.setEmail("test@test.com");

        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setUsername(user.getUsername());
        signUpRequest.setEmail(user.getEmail());
        signUpRequest.setFull_name(user.getFull_name());
        signUpRequest.setPassword(user.getPassword());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setPassword(user.getPassword());
        loginRequest.setUsername(user.getUsername());

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            mock.perform(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(signUpRequest)));
            mock.perform(post("/api/auth/signin").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(loginRequest)));
            this.userEntity = userRepository.findByUsername("test");
            this.roomEntity = roomRepository.save(new RoomEntity("test_room", this.userEntity.getId()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void set_Off() {
        roomRepository.delete(this.roomEntity);
        userRepository.delete(this.userEntity);
    }

    @Test
    void getRooms() throws Exception {
        set_Up();
        
        RoomEntity roomEntity1 = roomRepository.save(new RoomEntity("test_room01", this.userEntity.getId()));
        RoomEntity roomEntity2 = roomRepository.save(new RoomEntity("test_room02", this.userEntity.getId()));
        RoomEntity roomEntity3 = roomRepository.save(new RoomEntity("test_room03", this.userEntity.getId()));

        mock.perform(post("/rooms/createRoom")).andExpect(status().isUnauthorized());
        mock.perform(post("/rooms/createRoom").header("authorization", "Bearer "+userEntity.getToken()+"f")).andExpect(status().isUnauthorized());
        mock.perform(post("/api/files/DenyFile/"+file2.getId()+"?roomId="+this.roomEntity.getId()).header("authorization", "Bearer "+userEntity.getToken())).andExpect(status().isOk());
        var result = mock.perform(post("/rooms/createRoom").header("authorization", "Bearer " + userEntity.getToken())).andExpect(status().isOk()).andReturn();

        set_Off();
    }

    @Test
    void getRoomByToken() {
        set_Up();

        set_Off();
    }

    @Test
    void createRoom() {
        set_Up();

        set_Off();
    }

    @Test
    void checkIfIsAdmin() {
        set_Up();

        set_Off();
    }

    @Test
    void joinRoom() {
        set_Up();

        set_Off();
    }
}
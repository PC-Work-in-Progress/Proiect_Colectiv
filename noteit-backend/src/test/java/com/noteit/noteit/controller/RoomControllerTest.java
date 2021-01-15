package com.noteit.noteit.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.noteit.noteit.NoteitApplicationTests;
import com.noteit.noteit.dtos.RoomDto;
import com.noteit.noteit.entities.RoomEntity;
import com.noteit.noteit.entities.UserRoomEntity;
import com.noteit.noteit.payload.LoginRequest;
import com.noteit.noteit.payload.SignUpRequest;
import com.noteit.noteit.repositories.RoomRepository;
import com.noteit.noteit.repositories.UserRepository;
import com.noteit.noteit.repositories.UserRoomRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.noteit.noteit.entities.UserEntity;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

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
    @Autowired
    private UserRoomRepository userRoomRepository;


    private UserEntity userEntity;
    private RoomEntity roomEntity1;
    private RoomEntity roomEntity2;
    private UserRoomEntity userRoomEntity1;
    private UserRoomEntity userRoomEntity2;
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
            this.roomEntity1 = roomRepository.save(new RoomEntity("test_room01", this.userEntity.getId()));
            this.roomEntity2 = roomRepository.save(new RoomEntity("test_room02", this.userEntity.getId()));
            this.userRoomEntity1 = userRoomRepository.save(new UserRoomEntity(this.userEntity.getId(), roomEntity1.getId()));
            this.userRoomEntity2 = userRoomRepository.save(new UserRoomEntity(this.userEntity.getId(), roomEntity2.getId()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void set_Off() {
        userRoomRepository.delete(this.userRoomEntity1);
        userRoomRepository.delete(this.userRoomEntity2);
        roomRepository.delete(this.roomEntity1);
        roomRepository.delete(this.roomEntity2);
        userRepository.delete(this.userEntity);
    }

    @Test
    void getRooms() throws Exception {
        set_Up();

        var result = mock.perform(get("/rooms/").header("authorization", "Bearer " + userEntity.getToken())).andExpect(status().isOk()).andReturn();
        String json = result.getResponse().getContentAsString();

        assert !json.equals("[]");

        int foundRoom = 0;

        List<RoomDto> roomDtoList = mapper.readValue(json, new TypeReference<List<RoomDto>>(){});

        for (RoomDto roomDto : roomDtoList)
        {
            if (roomDto.getId().equals(roomEntity1.getId()) && roomDto.getName().equals(roomEntity1.getName()))
            {
                foundRoom++;
            }
            if (roomDto.getId().equals(roomEntity2.getId()) && roomDto.getName().equals(roomEntity2.getName()))
            {
                foundRoom++;
            }
        }

        assert foundRoom == 2;

        set_Off();
    }

    @Test
    void getRoomsByToken() throws Exception {
        set_Up();

        var result = mock.perform(get("/rooms/roomsUser").header("authorization", "Bearer " + userEntity.getToken())).andExpect(status().isOk()).andReturn();
        String json = result.getResponse().getContentAsString();

        assert !json.equals("[]");

        List<RoomDto> roomDtoList = mapper.readValue(json, new TypeReference<List<RoomDto>>(){});

        assert roomDtoList.size() == 2;

        int foundRoom = 0;

        if (roomDtoList.get(0).getId().equals(roomEntity1.getId()) && roomDtoList.get(0).getName().equals(roomEntity1.getName()))
        {
            foundRoom++;
        }
        if (roomDtoList.get(1).getId().equals(roomEntity2.getId()) && roomDtoList.get(1).getName().equals(roomEntity2.getName()))
        {
            foundRoom++;
        }

        assert foundRoom == 2;

        set_Off();
    }

    @Test
    void createRoom() throws Exception {
        set_Up();

        /*
        var result = mock.perform(post("/rooms/createRoom").header("authorization", "Bearer " + userEntity.getToken())).andExpect(status().isOk()).andReturn();
        String json = result.getResponse().getContentAsString();
        */



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
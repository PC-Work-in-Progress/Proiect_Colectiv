package com.noteit.noteit.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.noteit.noteit.NoteitApplicationTests;
import com.noteit.noteit.files.model.*;
import com.noteit.noteit.files.repository.FileTagRepository;
import com.noteit.noteit.rooms.dtos.RoomDto;
import com.noteit.noteit.files.repository.FileDBRepository;
import com.noteit.noteit.files.repository.FileRoomDBRepository;
import com.noteit.noteit.authentication.message.LoginRequest;
import com.noteit.noteit.authentication.message.SignUpRequest;
import com.noteit.noteit.rooms.model.RoomEntity;
import com.noteit.noteit.rooms.model.UserRoomEntity;
import com.noteit.noteit.rooms.repository.RoomRepository;
import com.noteit.noteit.rooms.repository.UserRoomRepository;
import com.noteit.noteit.tags.model.TagEntity;
import com.noteit.noteit.tags.repository.TagRepository;
import com.noteit.noteit.users.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.noteit.noteit.users.model.UserEntity;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    @Autowired
    private FileTagRepository fileTagRepository;
    @Autowired
    private FileRoomDBRepository fileRoomRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private FileDBRepository fileRepository;

    private UserEntity userEntity1;
    private UserEntity userEntity2;
    private RoomEntity roomEntity1;
    private RoomEntity roomEntity2;
    private UserRoomEntity userRoomEntity1;
    private UserRoomEntity userRoomEntity2;
    private ObjectMapper mapper = new ObjectMapper();

    public void set_Up() {
        var user1 = new UserEntity();
        user1.setUsername("test1");
        user1.setPassword("test1Parola");
        user1.setFull_name("test1 test1");
        user1.setEmail("test1@test1.com");

        SignUpRequest signUpRequest1 = new SignUpRequest();
        signUpRequest1.setUsername(user1.getUsername());
        signUpRequest1.setEmail(user1.getEmail());
        signUpRequest1.setFull_name(user1.getFull_name());
        signUpRequest1.setPassword(user1.getPassword());

        LoginRequest loginRequest1 = new LoginRequest();
        loginRequest1.setPassword(user1.getPassword());
        loginRequest1.setUsername(user1.getUsername());

        var user2 = new UserEntity();
        user2.setUsername("test2");
        user2.setPassword("test2Parola");
        user2.setFull_name("test2 test2");
        user2.setEmail("test2@test2.com");

        SignUpRequest signUpRequest2 = new SignUpRequest();
        signUpRequest2.setUsername(user2.getUsername());
        signUpRequest2.setEmail(user2.getEmail());
        signUpRequest2.setFull_name(user2.getFull_name());
        signUpRequest2.setPassword(user2.getPassword());

        LoginRequest loginRequest2 = new LoginRequest();
        loginRequest2.setPassword(user2.getPassword());
        loginRequest2.setUsername(user2.getUsername());

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            mock.perform(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(signUpRequest1)));
            mock.perform(post("/api/auth/signin").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(loginRequest1)));
            this.userEntity1 = userRepository.findByUsername("test1");

            mock.perform(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(signUpRequest2)));
            mock.perform(post("/api/auth/signin").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(loginRequest2)));
            this.userEntity2 = userRepository.findByUsername("test2");

            this.roomEntity1 = roomRepository.save(new RoomEntity("test_room01", this.userEntity1.getId()));
            this.roomEntity2 = roomRepository.save(new RoomEntity("test_room02", this.userEntity1.getId()));

            this.userRoomEntity1 = userRoomRepository.save(new UserRoomEntity(this.userEntity1.getId(), roomEntity1.getId()));
            this.userRoomEntity2 = userRoomRepository.save(new UserRoomEntity(this.userEntity1.getId(), roomEntity2.getId()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void set_Off() {
        userRoomRepository.delete(this.userRoomEntity1);
        userRoomRepository.delete(this.userRoomEntity2);
        roomRepository.delete(this.roomEntity1);
        roomRepository.delete(this.roomEntity2);
        userRepository.delete(this.userEntity1);
        userRepository.delete(this.userEntity2);
    }

    @Test
    void getRooms() throws Exception {
        set_Up();

        var result = mock.perform(
                get("/rooms/")
                        .header("authorization", "Bearer " + userEntity1.getToken()))
                .andExpect(status()
                        .isOk())
                .andReturn();

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
    void getRoomByToken() throws Exception {
        set_Up();

        var result = mock.perform(
                get("/rooms/roomsUser")
                        .header("authorization", "Bearer " + userEntity1.getToken()))
                .andExpect(status()
                        .isOk())
                .andReturn();

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

        RoomDto roomDtoBody = new RoomDto("", "test_room03");

        String jsonBody = new ObjectMapper().writeValueAsString(roomDtoBody);

        var result = mock.perform(
                MockMvcRequestBuilders.post("/rooms/createRoom")
                        .header("authorization", "Bearer " + userEntity1.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody)).andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();

        assert !json.equals("[]");

        RoomDto roomDto = mapper.readValue(json, new TypeReference<RoomDto>(){});

        Optional<RoomEntity> roomEntity = roomRepository.findById(roomDto.getId());

        if (roomEntity.isPresent())
        {
            UserRoomEntity userRoomEntity3 = userRoomRepository.findUserRoomEntityByUserRoomId_RoomId(roomEntity.get().getId());
            userRoomRepository.delete(userRoomEntity3);
            roomRepository.delete(roomEntity.get());
            assert true;
        }
        else
        {
            assert false;
        }

        set_Off();
    }

    @Test
    void checkIfIsAdmin() throws Exception {
        set_Up();

        var result = mock.perform(
                get("/rooms/isAdmin/" + roomEntity1.getId())
                        .header("authorization", "Bearer " + userEntity1.getToken()))
                .andExpect(status()
                        .isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        assert !json.equals("[]");

        assert json.contains("true");

        set_Off();
    }

    @Test
    void joinRoom() throws Exception {
        set_Up();

        var result = mock.perform(
                post("/rooms/joinRoom/" + roomEntity1.getId())
                        .header("authorization", "Bearer " + userEntity2.getToken()))
                .andExpect(status()
                        .isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        assert json.contains("User joined room");

        UserRoomEntity userRoomEntityTest = userRoomRepository.findUserRoomEntityByUserRoomId_UserId(userEntity2.getId()).get(0);

        assert userRoomEntityTest != null;

        userRoomRepository.delete(userRoomEntityTest);

        set_Off();
    }

    @Test
    void filterRoomsByTags() throws Exception {
        set_Up();
        var room1 = roomRepository.save(new RoomEntity("test_room_1", userEntity1.getId()));
        var room2 = roomRepository.save(new RoomEntity("test_room_2", userEntity1.getId()));
        var tag1 = new TagEntity();
        tag1.setName("test_tag_1");
        tag1.setPredefined(1);
        var tag2 = new TagEntity();
        tag2.setName("test_tag_2");
        tag2.setPredefined(1);
        tagRepository.save(tag1);
        tagRepository.save(tag2);
        var file1 = fileRepository
                .save(new FileDB("test_file_1", "txt", new byte[12], "15/01/2021"));
        var file2 = fileRepository
                .save(new FileDB("test_file_2", "txt", new byte[12], "15/01/2021"));
        var fileRoom1 = fileRoomRepository
                .save(new FileRoomDB(new FileRoomCompositePK(room1.getId(), file1.getId(), userEntity1.getId())));
        var fileRoom2 = fileRoomRepository
                .save(new FileRoomDB(new FileRoomCompositePK(room2.getId(), file2.getId(), userEntity1.getId())));
        var fileTag1 = fileTagRepository
                .save(new FileTagEntity(new FileTagPK(file1.getId(), tag1.getId())));
        var fileTag2 = fileTagRepository
                .save(new FileTagEntity(new FileTagPK(file2.getId(), tag2.getId())));
        mock.perform(get("/rooms/filterRooms/test_tag_1")).andExpect(status().isUnauthorized());
        var result = mock.perform(get("/rooms/filterRooms/test_tag_1").header("authorization", "Bearer " + userEntity1.getToken())).andExpect(status().isOk()).andReturn();
        String json = result.getResponse().getContentAsString();
        List<Map<String, String>> list = mapper.readValue(json, List.class);
        assert list.size() == 1;
        result = mock.perform(get("/rooms/filterRooms/test_tag_1,test_tag_2").header("authorization", "Bearer " + userEntity1.getToken())).andExpect(status().isOk()).andReturn();
        json = result.getResponse().getContentAsString();
        list = mapper.readValue(json, List.class);
        assert list.size() == 2;
        fileRoomRepository.delete(fileRoom1);
        fileRoomRepository.delete(fileRoom2);
        fileTagRepository.delete(fileTag1);
        fileTagRepository.delete(fileTag2);
        roomRepository.delete(room1);
        roomRepository.delete(room2);
        tagRepository.delete(tag1);
        tagRepository.delete(tag2);
        fileRepository.delete(file1);
        fileRepository.delete(file2);
        set_Off();
    }


    @Test
    void searchRoomsByName() throws Exception {
        set_Up();
        var room1 = new RoomEntity();
        room1.setName("test_room_1");
        room1.setOwnerId(userEntity1.getId());
        var room2 = new RoomEntity();
        room2.setName("test_room_2");
        room2.setOwnerId(userEntity1.getId());
        var room3 = new RoomEntity();
        room3.setName("test_room_3");
        room3.setOwnerId(userEntity1.getId());
        roomRepository.save(room1);
        roomRepository.save(room2);
        roomRepository.save(room3);
        mock.perform(get("/rooms/test_room_1")).andExpect(status().isUnauthorized());
        mock.perform(get("/rooms/test_room_1").header("authorization", "Bearer " + userEntity1.getToken() + "test")).andExpect(status().isUnauthorized());
        var result = mock.perform(get("/rooms/test_room_").header("authorization", "Bearer " + userEntity1.getToken())).andExpect(status().isOk()).andReturn();
        String json = result.getResponse().getContentAsString();
        List<Map<String, String>> list = mapper.readValue(json, List.class);
        assert list.size() == 3;
        result = mock.perform(get("/rooms/test_room_1").header("authorization", "Bearer " + userEntity1.getToken())).andExpect(status().isOk()).andReturn();
        json = result.getResponse().getContentAsString();
        list = mapper.readValue(json, List.class);
        assert list.size() == 1;
        roomRepository.delete(room1);
        roomRepository.delete(room2);
        roomRepository.delete(room3);
        set_Off();
    }
}
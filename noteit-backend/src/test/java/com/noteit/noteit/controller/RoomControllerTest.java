package com.noteit.noteit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noteit.noteit.NoteitApplicationTests;
import com.noteit.noteit.entities.*;
import com.noteit.noteit.files.model.FileDB;
import com.noteit.noteit.files.model.FileRoomCompositePK;
import com.noteit.noteit.files.model.FileRoomDB;
import com.noteit.noteit.files.repository.FileDBRepository;
import com.noteit.noteit.files.repository.FileRoomDBRepository;
import com.noteit.noteit.payload.LoginRequest;
import com.noteit.noteit.payload.SignUpRequest;
import com.noteit.noteit.repositories.FileTagRepository;
import com.noteit.noteit.repositories.RoomRepository;
import com.noteit.noteit.repositories.TagRepository;
import com.noteit.noteit.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

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
    private TagRepository tagRepository;
    @Autowired
    private FileDBRepository fileRepository;
    @Autowired
    private FileRoomDBRepository fileRoomRepository;
    @Autowired
    private FileTagRepository fileTagRepository;


    private UserEntity userEntity1;
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
            this.userEntity1 = userRepository.findByUsername("test");
            this.roomEntity = roomRepository.save(new RoomEntity("test_room", this.userEntity1.getId()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void set_Off() {
        roomRepository.delete(this.roomEntity);
        userRepository.delete(this.userEntity1);
    }

//    @Test
//    void getRooms() throws Exception {
//        set_Up();
//
//        RoomEntity roomEntity1 = roomRepository.save(new RoomEntity("test_room01", this.userEntity.getId()));
//        RoomEntity roomEntity2 = roomRepository.save(new RoomEntity("test_room02", this.userEntity.getId()));
//        RoomEntity roomEntity3 = roomRepository.save(new RoomEntity("test_room03", this.userEntity.getId()));
//
//        mock.perform(post("/rooms/createRoom")).andExpect(status().isUnauthorized());
//        mock.perform(post("/rooms/createRoom").header("authorization", "Bearer "+userEntity.getToken()+"f")).andExpect(status().isUnauthorized());
//        mock.perform(post("/api/files/DenyFile/"+file2.getId()+"?roomId="+this.roomEntity.getId()).header("authorization", "Bearer "+userEntity.getToken())).andExpect(status().isOk());
//        var result = mock.perform(post("/rooms/createRoom").header("authorization", "Bearer " + userEntity.getToken())).andExpect(status().isOk()).andReturn();
//
//        set_Off();
//    }

//    @Test
//    void getRoomByToken() {
//        set_Up();
//
//        set_Off();
//    }
//
//    @Test
//    void createRoom() {
//        set_Up();
//
//        set_Off();
//    }
//
//    @Test
//    void checkIfIsAdmin() {
//        set_Up();
//
//        set_Off();
//    }

//    @Test
//    void joinRoom() {
//        set_Up();
//
//        set_Off();
//    }

    @Test
    void filterRoomsByTags() throws Exception{
        set_Up();

        var room1=roomRepository.save(new RoomEntity("test_room_1", userEntity1.getId()));
        var room2=roomRepository.save(new RoomEntity("test_room_2", userEntity1.getId()));

        var tag1=new TagEntity();
        tag1.setName("test_tag_1");
        tag1.setPredefined(1);
        var tag2=new TagEntity();
        tag2.setName("test_tag_2");
        tag2.setPredefined(1);
        tagRepository.save(tag1);
        tagRepository.save(tag2);

        var file1=fileRepository
                .save(new FileDB("test_file_1","txt",new byte[12],"15/01/2021"));
        var file2=fileRepository
                .save(new FileDB("test_file_2","txt",new byte[12],"15/01/2021"));

        var fileRoom1= fileRoomRepository
                .save(new FileRoomDB(new FileRoomCompositePK(room1.getId(), file1.getId(), userEntity1.getId())));

        var fileRoom2=fileRoomRepository
                .save(new FileRoomDB(new FileRoomCompositePK(room2.getId(), file2.getId(), userEntity1.getId())));



        var fileTag1=fileTagRepository
                .save(new FileTagEntity(new FileTagPK(file1.getId(), tag1.getId())));

        var fileTag2=fileTagRepository
                .save(new FileTagEntity(new FileTagPK(file2.getId(), tag2.getId())));



        mock.perform(get("/rooms/filterRooms/test_tag_1")).andExpect(status().isUnauthorized());

        var result = mock.perform(get("/rooms/filterRooms/test_tag_1").header("authorization", "Bearer " + userEntity1.getToken())).andExpect(status().isOk()).andReturn();
        String json = result.getResponse().getContentAsString();

        List<Map<String,String>> list = mapper.readValue(json, List.class);

        assert list.size()==1;



        result = mock.perform(get("/rooms/filterRooms/test_tag_1,test_tag_2").header("authorization", "Bearer " + userEntity1.getToken())).andExpect(status().isOk()).andReturn();
        json = result.getResponse().getContentAsString();

        list = mapper.readValue(json, List.class);

        assert list.size()==2;


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
    void searchRoomsByName() throws Exception{
        set_Up();
        var room1=new RoomEntity();
        room1.setName("test_room_1");
        room1.setOwnerId(userEntity1.getId());

        var room2=new RoomEntity();
        room2.setName("test_room_2");
        room2.setOwnerId(userEntity1.getId());

        var room3=new RoomEntity();
        room3.setName("test_room_3");
        room3.setOwnerId(userEntity1.getId());

        roomRepository.save(room1);
        roomRepository.save(room2);
        roomRepository.save(room3);

        mock.perform(get("/rooms/test_room_1")).andExpect(status().isUnauthorized());
        mock.perform(get("/rooms/test_room_1").header("authorization", "Bearer " + userEntity1.getToken() + "test")).andExpect(status().isUnauthorized());

        var result = mock.perform(get("/rooms/test_room_").header("authorization", "Bearer " + userEntity1.getToken())).andExpect(status().isOk()).andReturn();
        String json = result.getResponse().getContentAsString();

        List<Map<String,String>> list = mapper.readValue(json, List.class);

        assert list.size()==3;



        result = mock.perform(get("/rooms/test_room_1").header("authorization", "Bearer " + userEntity1.getToken())).andExpect(status().isOk()).andReturn();
        json = result.getResponse().getContentAsString();

        list = mapper.readValue(json, List.class);

        assert list.size()==1;

        roomRepository.delete(room1);
        roomRepository.delete(room2);
        roomRepository.delete(room3);
        set_Off();
    }
}
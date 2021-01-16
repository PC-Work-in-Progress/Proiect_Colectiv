package com.noteit.noteit.notifications.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.noteit.noteit.NoteitApplicationTests;
import com.noteit.noteit.rooms.dtos.RoomDto;
import com.noteit.noteit.files.model.FileDB;
import com.noteit.noteit.files.model.FileRoomCompositePK;
import com.noteit.noteit.files.model.FileRoomDB;
import com.noteit.noteit.files.repository.FileDBRepository;
import com.noteit.noteit.files.repository.FileRoomDBRepository;
import com.noteit.noteit.notifications.repository.NotificationsRepository;
import com.noteit.noteit.authentication.message.LoginRequest;
import com.noteit.noteit.authentication.message.SignUpRequest;
import com.noteit.noteit.rooms.repository.RoomRepository;
import com.noteit.noteit.users.repository.UserRepository;
import com.noteit.noteit.rooms.model.RoomEntity;
import com.noteit.noteit.users.model.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class NotificationsControllerTest extends NoteitApplicationTests{
    @Autowired
    private MockMvc mock;

    @Autowired private FileDBRepository fileDBRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private RoomRepository roomRepository;
    @Autowired private FileRoomDBRepository fileRoomDBRepository;
    @Autowired private NotificationsRepository notificationsRepository;


    private FileDB file;
    private UserEntity userEntity;
    private RoomEntity roomEntity;
    private FileRoomDB fileRoomDB = new FileRoomDB();
    private ObjectMapper mapper = new ObjectMapper();

    public void set_Up(){

        var u = new UserEntity();
        u.setUsername("test");
        u.setPassword("testParola");
        u.setFull_name("test test");
        u.setEmail("test@test.com");

        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setUsername(u.getUsername());
        signUpRequest.setEmail(u.getEmail());
        signUpRequest.setFull_name(u.getFull_name());
        signUpRequest.setPassword(u.getPassword());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setPassword(u.getPassword());
        loginRequest.setUsername(u.getUsername());

        RoomDto roomDto = new RoomDto();
        roomDto.setName("testRoom");

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            mock.perform(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(signUpRequest)));
            mock.perform(post("/api/auth/signin").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(loginRequest)));
            this.userEntity = userRepository.findByUsername("test");
            this.file =fileDBRepository.save(new FileDB("file_test", "",new byte[11], ""));
            this.roomEntity = roomRepository.save(new RoomEntity("test_room", this.userEntity.getId()));
            this.fileRoomDB.setId(new FileRoomCompositePK(this.roomEntity.getId(),this.file.getId(), this.userEntity.getId()));
            this.fileRoomDB = fileRoomDBRepository.save(this.fileRoomDB);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    void setOff(){
        fileRoomDBRepository.delete(fileRoomDBRepository.findById_FileIdAndId_RoomId(this.file.getId(), this.roomEntity.getId()).get(0));
        fileDBRepository.delete(this.file);
        roomRepository.delete(this.roomEntity);
        userRepository.delete(this.userEntity);
    }
    @Test
    void getNotifications() throws Exception {
        set_Up();
        var file2 =fileDBRepository.save(new FileDB("test22", "",new byte[12], ""));
        var fileRoom2 = new FileRoomDB();
        fileRoom2.setId(new FileRoomCompositePK(this.roomEntity.getId(),file2.getId(), this.userEntity.getId()));
        fileRoom2 = fileRoomDBRepository.save(fileRoom2);
        mock.perform(get("/api/notifications/GetNotifications")).andExpect(status().isUnauthorized());
        mock.perform(get("/api/notifications/GetNotifications").header("authorization", "Bearer "+userEntity.getToken()+"f")).andExpect(status().isUnauthorized());
        mock.perform(put("/api/files/DenyFile/"+file2.getId()+"?roomId="+this.roomEntity.getId()).header("authorization", "Bearer "+userEntity.getToken())).andExpect(status().isOk());

        var list = mock.perform(get("/api/notifications/GetNotifications").header("authorization", "Bearer "+userEntity.getToken())).andExpect(status().isOk()).andReturn();
        String json = list.getResponse().getContentAsString();
        assert !json.equals("[]");
        var notif = notificationsRepository.findByUserId(this.userEntity.getId()).get(0);
        notificationsRepository.delete(notif);
        fileRoomDBRepository.delete(fileRoom2);
        fileDBRepository.delete(file2);
        setOff();
    }

    @Test
    void readNotification() throws Exception {
        set_Up();
        var file2 =fileDBRepository.save(new FileDB("test22", "",new byte[12], ""));
        var fileRoom2 = new FileRoomDB();
        fileRoom2.setId(new FileRoomCompositePK(this.roomEntity.getId(),file2.getId(), this.userEntity.getId()));
        fileRoom2 = fileRoomDBRepository.save(fileRoom2);
        mock.perform(put("/api/notifications/ReadNotification/tst")).andExpect(status().isUnauthorized());
        mock.perform(put("/api/notifications/ReadNotification/tst").header("authorization", "Bearer "+userEntity.getToken()+"f")).andExpect(status().isUnauthorized());
        mock.perform(put("/api/notifications/ReadNotification/tst").header("authorization", "Bearer "+userEntity.getToken())).andExpect(status().isBadRequest());
        mock.perform(put("/api/files/DenyFile/"+file2.getId()+"?roomId="+this.roomEntity.getId()).header("authorization", "Bearer "+userEntity.getToken())).andExpect(status().isOk());
        var notif = notificationsRepository.findByUserId(this.userEntity.getId()).get(0);
        mock.perform(put("/api/notifications/ReadNotification/"+notif.getId()).header("authorization", "Bearer "+userEntity.getToken())).andExpect(status().isOk());
        mock.perform(put("/api/notifications/ReadNotification/"+notif.getId()).header("authorization", "Bearer "+userEntity.getToken())).andExpect(status().isConflict());
        notif = notificationsRepository.findById(notif.getId()).get();
        assert notif.getViewed()==1;
        notificationsRepository.delete(notif);
        fileRoomDBRepository.delete(fileRoom2);
        fileDBRepository.delete(file2);
        setOff();
    }
}
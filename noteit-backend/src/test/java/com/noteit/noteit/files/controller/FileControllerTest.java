package com.noteit.noteit.files.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.noteit.noteit.NoteitApplicationTests;
import com.noteit.noteit.dtos.RoomDto;
import com.noteit.noteit.entities.*;
import com.noteit.noteit.files.dtos.FileRoomDto;
import com.noteit.noteit.files.model.FileDB;
import com.noteit.noteit.files.model.FileRoomCompositePK;
import com.noteit.noteit.files.model.FileRoomDB;
import com.noteit.noteit.files.repository.FileDBRepository;
import com.noteit.noteit.files.repository.FileRoomDBRepository;
import com.noteit.noteit.notifications.repository.NotificationsRepository;
import com.noteit.noteit.payload.LoginRequest;
import com.noteit.noteit.payload.SignUpRequest;
import com.noteit.noteit.repositories.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class FileControllerTest extends NoteitApplicationTests {

    @Autowired
    private MockMvc mock;

    @Autowired private FileDBRepository fileDBRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private RoomRepository roomRepository;
    @Autowired private FileRoomDBRepository fileRoomDBRepository;
    @Autowired private TagRepository tagRepository;
    @Autowired private FileTagRepository fileTagRepository;
    @Autowired private NotificationsRepository notificationsRepository;
    @Autowired private UserRoomRepository userRoomRepository;


    private FileDB file;
    private UserEntity userEntity;
    private RoomEntity roomEntity;
    private UserRoomEntity userRoomEntity;
    private TagEntity tagEntity;
    private FileTagEntity fileTagEntity;
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


            tagRepository.save(new TagEntity("tagTest", 1));
            this.tagEntity = tagRepository.findByName("tagTest");
            this.fileTagEntity = fileTagRepository.save(new FileTagEntity(new FileTagPK(file.getId(), tagEntity.getId())));

            this.roomEntity = roomRepository.save(new RoomEntity("test_room", this.userEntity.getId()));
            this.userRoomEntity = userRoomRepository.save(new UserRoomEntity(userEntity.getId(), roomEntity.getId()));
            this.fileRoomDB.setId(new FileRoomCompositePK(this.roomEntity.getId(),this.file.getId(), this.userEntity.getId()));
            this.fileRoomDB = fileRoomDBRepository.save(this.fileRoomDB);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    void setOff(){
        fileTagRepository.delete(fileTagEntity);
        tagRepository.delete(tagEntity);
        userRoomRepository.delete(this.userRoomEntity);
        fileRoomDBRepository.delete(fileRoomDBRepository.findById_FileIdAndId_RoomId(this.file.getId(), this.roomEntity.getId()).get(0));
        fileDBRepository.delete(this.file);
        roomRepository.delete(this.roomEntity);
        userRepository.delete(this.userEntity);
    }


    @Test
    void acceptFile() throws Exception {
        set_Up();
        mock.perform(put("/api/files/AcceptFile/"+file.getId()+"?roomId="+this.roomEntity.getId())).andExpect(status().isUnauthorized());
        mock.perform(put("/api/files/AcceptFile/"+file.getId()+"?roomId="+this.roomEntity.getId()).header("authorization", "Bearer "+userEntity.getToken()+"f")).andExpect(status().isUnauthorized());
        mock.perform(put("/api/files/AcceptFile/"+file.getId()+"f"+"?roomId="+this.roomEntity.getId()).header("authorization", "Bearer "+userEntity.getToken())).andExpect(status().isBadRequest());
        mock.perform(put("/api/files/AcceptFile/"+file.getId()+"?roomId="+this.roomEntity.getId()+"f").header("authorization", "Bearer "+userEntity.getToken())).andExpect(status().isBadRequest());
        mock.perform(put("/api/files/AcceptFile/"+file.getId()+"?roomId="+this.roomEntity.getId()).header("authorization", "Bearer "+userEntity.getToken())).andExpect(status().isOk());
        mock.perform(put("/api/files/AcceptFile/"+file.getId()+"?roomId="+this.roomEntity.getId()).header("authorization", "Bearer "+userEntity.getToken())).andExpect(status().isBadRequest());

        setOff();
    }

    @Test
    void uploadFile() throws Exception {
        set_Up();
        mock.perform(post("/api/files/UploadFile")).andExpect(status().isUnauthorized());
        mock.perform(post("/api/files/UploadFile").header("authorization", "Bearer "+userEntity.getToken()+"f")).andExpect(status().isUnauthorized());

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "file_test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "test".getBytes()
        );
        mock.perform(multipart("/api/files/UploadFile?roomId=fakeId&tags=tag1,tag2").file(file).header("authorization", "Bearer "+userEntity.getToken())).andExpect(status().isBadRequest());


        var rez = mock.perform(multipart("/api/files/UploadFile?tags=tag1ff,tag2ff&roomId="+this.roomEntity.getId()).file(file).header("authorization", "Bearer "+userEntity.getToken())).andExpect(status().isOk()).andReturn();
        String json = rez.getResponse().getContentAsString();
        Map<String,String> map = mapper.readValue(json, Map.class);
        String fileID = map.get("fileId");
        mock.perform(multipart("/api/files/UploadFile?tags=tag1,tag2&roomId="+this.roomEntity.getId()).file(file).header("authorization", "Bearer "+userEntity.getToken())).andExpect(status().isExpectationFailed());

        var room2 = roomRepository.save(new RoomEntity("test_room2", this.userEntity.getId()));
        mock.perform(multipart("/api/files/UploadFile?tags=tag1ff,tag2ff&roomId="+room2.getId()).file(file).header("authorization", "Bearer "+userEntity.getToken())).andExpect(status().isOk());


        var fileRooms = fileRoomDBRepository.findById_FileId(fileID);
        for (var fr: fileRooms){
            fileRoomDBRepository.delete(fr);
        }
        roomRepository.delete(room2);
        var t1 = tagRepository.findByName("tag1ff");
        var t2 = tagRepository.findByName("tag2ff");
        var t1List = fileTagRepository.findById_TagId(t1.getId()).get();
        var t2List = fileTagRepository.findById_TagId(t2.getId()).get();
        for (FileTagEntity fileTagEntity : t1List){
            fileTagRepository.delete(fileTagEntity);
        }

        for (FileTagEntity fileTagEntity : t2List){
            fileTagRepository.delete(fileTagEntity);
        }

        tagRepository.delete(t1);
        tagRepository.delete(t2);
        fileDBRepository.delete(fileDBRepository.findById(fileID).get());
        setOff();
    }

    @Test
    void getApprovedFilesForRoom() throws Exception {
        set_Up();
        mock.perform(get("/api/files/ApprovedFiles?roomId="+roomEntity.getId())).andExpect(status().isUnauthorized());
        mock.perform(get("/api/files/ApprovedFiles?roomId="+roomEntity.getId()+"f").header("authorization", "Bearer "+userEntity.getToken())).andExpect(status().isBadRequest());
        mock.perform(get("/api/files/ApprovedFiles?roomId="+roomEntity.getId()+"f").header("authorization", "Bearer "+userEntity.getToken()+"f")).andExpect(status().isUnauthorized());
        mock.perform(get("/api/files/ApprovedFiles").header("authorization", "Bearer "+userEntity.getToken())).andExpect(status().isBadRequest());
        mock.perform(put("/api/files/AcceptFile/"+file.getId()+"?roomId="+roomEntity.getId()).header("authorization", "Bearer "+userEntity.getToken()));
        var rez = mock.perform(get("/api/files/ApprovedFiles?roomId="+roomEntity.getId()).header("authorization", "Bearer "+userEntity.getToken())).andExpect(status().isOk()).andReturn();
        String json = rez.getResponse().getContentAsString();
        assert !json.equals("[]");
        setOff();
    }

    @Test
    void getInReviewFiles() throws Exception {
        set_Up();
        mock.perform(get("/api/files/InReviewFiles?roomId="+roomEntity.getId())).andExpect(status().isUnauthorized());
        mock.perform(get("/api/files/InReviewFiles?roomId="+roomEntity.getId()+"f").header("authorization", "Bearer "+userEntity.getToken())).andExpect(status().isBadRequest());
        mock.perform(get("/api/files/InReviewFiles?roomId="+roomEntity.getId()+"f").header("authorization", "Bearer "+userEntity.getToken()+"f")).andExpect(status().isUnauthorized());
        mock.perform(get("/api/files/InReviewFiles").header("authorization", "Bearer "+userEntity.getToken())).andExpect(status().isBadRequest());
        var rez = mock.perform(get("/api/files/InReviewFiles?roomId="+roomEntity.getId()).header("authorization", "Bearer "+userEntity.getToken())).andExpect(status().isOk()).andReturn();
        String json = rez.getResponse().getContentAsString();
        assert !json.equals("[]");
        setOff();
    }

    @Test
    void getFile() throws Exception {
        set_Up();
        mock.perform(get("/api/files/"+this.file.getId()+"?roomId="+this.roomEntity.getId())).andExpect(status().isUnauthorized());
        mock.perform(get("/api/files/"+this.file.getId()+"?roomId="+this.roomEntity.getId()).header("authorization", "Bearer "+userEntity.getToken()+"f")).andExpect(status().isUnauthorized());
        mock.perform(get("/api/files/"+this.file.getId()+"f"+"?roomId="+this.roomEntity.getId()).header("authorization", "Bearer "+userEntity.getToken())).andExpect(status().isExpectationFailed());
        mock.perform(get("/api/files/"+this.file.getId()+"?roomId="+this.roomEntity.getId()+"f").header("authorization", "Bearer "+userEntity.getToken())).andExpect(status().isBadRequest());
        mock.perform(get("/api/files/"+this.file.getId()+"?roomId="+this.roomEntity.getId()).header("authorization", "Bearer "+userEntity.getToken())).andExpect(status().isOk());
        setOff();
    }

    @Test
    void denyFile() throws Exception {
        set_Up();
        var file2 =fileDBRepository.save(new FileDB("test22", "",new byte[12], ""));
        var fileRoom2 = new FileRoomDB();
        fileRoom2.setId(new FileRoomCompositePK(this.roomEntity.getId(),file2.getId(), this.userEntity.getId()));
        fileRoom2 = fileRoomDBRepository.save(fileRoom2);

        var tag = new TagEntity();
        tag.setName("tst_tag");
        tag = tagRepository.save(tag);
        var fileTag = new FileTagEntity();
        fileTag.setId(new FileTagPK(file2.getId(), tag.getId()));
        fileTag = fileTagRepository.save(fileTag);

        mock.perform(put("/api/files/DenyFile/"+file2.getId()+"?roomId=tt")).andExpect(status().isUnauthorized());
        mock.perform(put("/api/files/DenyFile/"+file2.getId()+"?roomId=tt").header("authorization", "Bearer "+userEntity.getToken()+"f")).andExpect(status().isUnauthorized());
        mock.perform(put("/api/files/DenyFile/"+file2.getId()+"f"+"?roomId=tt").header("authorization", "Bearer "+userEntity.getToken())).andExpect(status().isBadRequest());
        mock.perform(put("/api/files/DenyFile/"+file2.getId()).header("authorization", "Bearer "+userEntity.getToken())).andExpect(status().isBadRequest());
        mock.perform(put("/api/files/DenyFile/"+file2.getId()+"?roomId=rr").header("authorization", "Bearer "+userEntity.getToken())).andExpect(status().isBadRequest());
        mock.perform(put("/api/files/DenyFile/"+file2.getId()+"?roomId="+this.roomEntity.getId()).header("authorization", "Bearer "+userEntity.getToken())).andExpect(status().isOk());

        var notif = notificationsRepository.findByUserId(this.userEntity.getId()).get(0);
        notificationsRepository.delete(notif);
        fileTagRepository.delete(fileTag);
        fileRoomDBRepository.delete(fileRoom2);
        fileDBRepository.delete(file2);
        tagRepository.delete(tag);
        setOff();

    }

    @Test
    void getDetailsFile() throws Exception {
        set_Up();

        FileDB fileTest = fileDBRepository.save(new FileDB("niceTest", "", new byte[20], ""));
        FileRoomDB fileRoomTest = new FileRoomDB();
        fileRoomTest.setId(new FileRoomCompositePK(this.roomEntity.getId(), fileTest.getId(), this.userEntity.getId()));
        fileRoomDBRepository.save(fileRoomTest);

        TagEntity tag = new TagEntity();
        tag.setName("niceTestTag");
        tagRepository.save(tag);
        FileTagEntity fileTag = new FileTagEntity();
        fileTag.setId(new FileTagPK(fileTest.getId(), tag.getId()));
        fileTagRepository.save(fileTag);

        mock.perform(get("/api/files/details/"+ this.roomEntity.getId() + "/" + this.file.getId())).andExpect(status().isUnauthorized());
        mock.perform(get("/api/files/details/"+ this.roomEntity.getId() + "/" + this.file.getId()).header("authorization", "Bearer " + userEntity.getToken() + "test")).andExpect(status().isUnauthorized());
        mock.perform(get("/api/files/details/"+ this.roomEntity.getId() + "test/" + this.file.getId()).header("authorization", "Bearer " + userEntity.getToken() + "test")).andExpect(status().isUnauthorized());
        mock.perform(get("/api/files/details/"+ this.roomEntity.getId() + "/" + this.file.getId() + "/").header("authorization", "Bearer " + userEntity.getToken() + "test")).andExpect(status().isUnauthorized());
        var result = mock.perform(get("/api/files/details/"+ this.roomEntity.getId() + "/" + fileTest.getId()).header("authorization", "Bearer " + userEntity.getToken())).andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        Map<String,String> map = mapper.readValue(json, Map.class);

        String name = map.get("name");
        String username = map.get("username");

        assert name.equals("niceTest");
        assert username.equals("test");

        fileRoomDBRepository.delete(fileRoomTest);
        fileTagRepository.delete(fileTag);
        fileDBRepository.delete(fileTest);
        tagRepository.delete(tag);

        setOff();
    }

    @Test
    void getRecentFilesFromToken() throws Exception {
        set_Up();

        var result = mock.perform(
                get("/api/files/recentFiles/0")
                        .header("authorization", "Bearer " + userEntity.getToken()))
                .andExpect(status()
                        .isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        assert !json.equals("[]");

        List<FileRoomDto> fileRoomDtoList = mapper.readValue(json, new TypeReference<List<FileRoomDto>>(){});

        assert fileRoomDtoList.size() == 1;

        FileRoomDto fileRoomDto = fileRoomDtoList.get(0);
        assert fileRoomDto.getUserName().equals("test test") && fileRoomDto.getRoomId().equals(roomEntity.getId())
                && fileRoomDto.getFileId().equals(file.getId());

        setOff();
    }

    @Test
    void getSearchedFilesByName() throws Exception {
        set_Up();

        var result = mock.perform(
                get("/api/files/filename/" + file.getName())
                        .header("authorization", "Bearer " + userEntity.getToken()))
                .andExpect(status()
                        .isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        assert !json.equals("[]");

        List<FileRoomDto> fileRoomDtoList = mapper.readValue(json, new TypeReference<List<FileRoomDto>>(){});

        assert fileRoomDtoList.size() == 1;

        FileRoomDto fileRoomDto = fileRoomDtoList.get(0);
        assert fileRoomDto.getUserName().equals("test test") && fileRoomDto.getRoomId().equals(roomEntity.getId())
                && fileRoomDto.getFileId().equals(file.getId()) && fileRoomDto.getFileName().equals(file.getName());

        setOff();
    }

    @Test
    void getSearchedFilesByTag() throws Exception {
        set_Up();

        String tagName = tagRepository.findById(fileTagEntity.getId().getTagId()).get().getName();

        var result = mock.perform(
                get("/api/files/tag/" + tagName)
                        .header("authorization", "Bearer " + userEntity.getToken()))
                .andExpect(status()
                        .isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        assert !json.equals("[]");

        List<FileRoomDto> fileRoomDtoList = mapper.readValue(json, new TypeReference<List<FileRoomDto>>(){});

        assert fileRoomDtoList.size() == 1;

        FileRoomDto fileRoomDto = fileRoomDtoList.get(0);
        assert fileRoomDto.getUserName().equals("test test") && fileRoomDto.getRoomId().equals(roomEntity.getId())
                && fileRoomDto.getFileId().equals(file.getId()) && fileRoomDto.getTags().contains(tagName);

        setOff();
    }
}
package com.noteit.noteit.tags.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noteit.noteit.NoteitApplicationTests;
import com.noteit.noteit.tags.model.TagEntity;
import com.noteit.noteit.users.model.UserEntity;
import com.noteit.noteit.authentication.message.LoginRequest;
import com.noteit.noteit.authentication.message.SignUpRequest;
import com.noteit.noteit.tags.repository.TagRepository;
import com.noteit.noteit.users.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TagsControllerTest extends NoteitApplicationTests {
    @Autowired
    private MockMvc mock;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TagRepository tagRepository;

    private UserEntity userEntity;
    private ObjectMapper mapper = new ObjectMapper();

    public void setUp() {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void setOff() {
        userRepository.delete(this.userEntity);
    }

    @Test
    void getAllPredefinedTags() throws Exception {
        setUp();
        var tag1=new TagEntity();
        tag1.setName("test_tag_1");
        tag1.setPredefined(1);

        var tag2=new TagEntity();
        tag2.setName("test_tag_2");
        tag2.setPredefined(0);

        tagRepository.save(tag1);
        tagRepository.save(tag2);

        mock.perform(get("/tags/predefined")).andExpect(status().isUnauthorized());
        mock.perform(get("/tags/predefined").header("authorization", "Bearer " + userEntity.getToken() + "test")).andExpect(status().isUnauthorized());

        var result = mock.perform(get("/tags/predefined").header("authorization", "Bearer " + userEntity.getToken())).andExpect(status().isOk()).andReturn();
        String json = result.getResponse().getContentAsString();

        List<Map<String,String>> list = mapper.readValue(json, List.class);

        assert list.stream().map(x->x.get("name")).collect(Collectors.toList()).contains("test_tag_1");

        tagRepository.delete(tag1);
        tagRepository.delete(tag2);
        setOff();
    }
}

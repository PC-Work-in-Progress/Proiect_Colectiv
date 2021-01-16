package com.noteit.noteit.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noteit.noteit.NoteitApplicationTests;
import com.noteit.noteit.entities.UserEntity;
import com.noteit.noteit.payload.LoginRequest;
import com.noteit.noteit.payload.SignUpRequest;
import com.noteit.noteit.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerTest extends NoteitApplicationTests {
    @Autowired
    private MockMvc mock;

    @Autowired
    private UserRepository userRepository;

    private UserEntity userEntity;
    private UserEntity user = new UserEntity();
    private ObjectMapper mapper = new ObjectMapper();

    public void setUp() {
        user.setUsername("test");
        user.setPassword("testParola");
        user.setFull_name("Test Test");
        user.setEmail("test@test.com");
    }

    void setOff() {
        userRepository.delete(this.userEntity);
    }

    @Test
    void signInTest() throws Exception {
        setUp();
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setPassword(user.getPassword());
        loginRequest.setUsername(user.getUsername());

        var result = mock.perform(put("/api/auth/signin").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(loginRequest))).andExpect(status().is4xxClientError()).andReturn();

        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setUsername(user.getUsername());
        signUpRequest.setEmail(user.getEmail());
        signUpRequest.setFull_name(user.getFull_name());
        signUpRequest.setPassword(user.getPassword());

        mock.perform(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(signUpRequest)));
        this.userEntity = userRepository.findByUsername("test");


        result = mock.perform(post("/api/auth/signin").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(loginRequest))).andExpect(status().is2xxSuccessful()).andReturn();
        String json = result.getResponse().getContentAsString();
        Map<String,String> map = mapper.readValue(json, Map.class);
        String message = map.get("accessToken");

        assert !message.equals("");

        setOff();
        userRepository.deleteUser(this.user.getUsername());
    }

    @Test
    void signUpTest() throws Exception {

        setUp();
        userRepository.deleteUser(this.user.getUsername());

        SignUpRequest signUpRequest = new SignUpRequest();

        // Invalid name
        signUpRequest.setUsername(user.getUsername());
        signUpRequest.setEmail(user.getEmail());
        signUpRequest.setFull_name("");
        signUpRequest.setPassword(user.getPassword());

        var result = mock.perform(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(signUpRequest))).andExpect(status().is2xxSuccessful()).andReturn();
        String json = result.getResponse().getContentAsString();
        Map<String,String> map = mapper.readValue(json, Map.class);
        String message = map.get("message");

        assert message.equals("Invalid name");


        // Invalid username
        signUpRequest.setUsername("");
        signUpRequest.setEmail(user.getEmail());
        signUpRequest.setFull_name(user.getFull_name());
        signUpRequest.setPassword(user.getPassword());

        result = mock.perform(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(signUpRequest))).andExpect(status().is2xxSuccessful()).andReturn();
        json = result.getResponse().getContentAsString();
        map = mapper.readValue(json, Map.class);
        message = map.get("message");

        assert message.equals("Invalid username");


        // Invalid password
        signUpRequest.setUsername(user.getUsername());
        signUpRequest.setEmail(user.getEmail());
        signUpRequest.setFull_name(user.getFull_name());
        signUpRequest.setPassword("");

        result = mock.perform(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(signUpRequest))).andExpect(status().is2xxSuccessful()).andReturn();
        json = result.getResponse().getContentAsString();
        map = mapper.readValue(json, Map.class);
        message = map.get("message");

        assert message.equals("Invalid password");


        // Invalid email
        signUpRequest.setUsername(user.getUsername());
        signUpRequest.setEmail("abc@gmail");
        signUpRequest.setFull_name(user.getFull_name());
        signUpRequest.setPassword(user.getPassword());

        result = mock.perform(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(signUpRequest))).andExpect(status().is2xxSuccessful()).andReturn();
        json = result.getResponse().getContentAsString();
        map = mapper.readValue(json, Map.class);
        message = map.get("message");

        assert message.equals("Invalid email");


        //Success case
        signUpRequest.setUsername(user.getUsername());
        signUpRequest.setEmail(user.getEmail());
        signUpRequest.setFull_name(user.getFull_name());
        signUpRequest.setPassword(user.getPassword());

        result = mock.perform(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(signUpRequest))).andExpect(status().is2xxSuccessful()).andReturn();
        this.userEntity = userRepository.findByUsername("test");
        json = result.getResponse().getContentAsString();
        map = mapper.readValue(json, Map.class);
        message = map.get("message");

        assert message.equals("User registered successfully");


        // Existing username
        signUpRequest.setUsername(user.getUsername());
        signUpRequest.setEmail(user.getEmail());
        signUpRequest.setFull_name(user.getFull_name());
        signUpRequest.setPassword(user.getPassword());

        result = mock.perform(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(signUpRequest))).andExpect(status().is2xxSuccessful()).andReturn();
        json = result.getResponse().getContentAsString();
        map = mapper.readValue(json, Map.class);
        message = map.get("message");

        assert message.equals("Username is already taken!");


        // Existing email
        signUpRequest.setUsername("newUser");
        signUpRequest.setEmail(user.getEmail());
        signUpRequest.setFull_name(user.getFull_name());
        signUpRequest.setPassword(user.getPassword());

        result = mock.perform(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(signUpRequest))).andExpect(status().is2xxSuccessful()).andReturn();
        json = result.getResponse().getContentAsString();
        map = mapper.readValue(json, Map.class);
        message = map.get("message");

        assert message.equals("Email Address already in use!");


        setOff();
        userRepository.deleteUser(this.user.getUsername());
    }
}

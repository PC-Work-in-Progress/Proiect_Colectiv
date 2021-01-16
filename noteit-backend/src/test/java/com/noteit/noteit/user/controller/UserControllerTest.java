package com.noteit.noteit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noteit.noteit.NoteitApplicationTests;
import com.noteit.noteit.users.model.UserEntity;
import com.noteit.noteit.authentication.message.LoginRequest;
import com.noteit.noteit.authentication.message.SignUpRequest;
import com.noteit.noteit.users.message.UpdateRequest;
import com.noteit.noteit.users.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTest extends NoteitApplicationTests {
    @Autowired
    private MockMvc mock;

    @Autowired
    private UserRepository userRepository;

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
    void getDetailsUser() throws Exception {
        setUp();
        mock.perform(post("/user/details")).andExpect(status().isUnauthorized());
        mock.perform(post("/user/details").header("authorization", "Bearer " + userEntity.getToken() + "test")).andExpect(status().isUnauthorized());

        var result = mock.perform(get("/user/details").header("authorization", "Bearer " + userEntity.getToken())).andExpect(status().isOk()).andReturn();
        String json = result.getResponse().getContentAsString();
        Map<String,String> map = mapper.readValue(json, Map.class);

        String username = map.get("username");
        String email = map.get("email");
        String fullName = map.get("full_name");

        assert username.equals("test");
        assert fullName.equals("test test");
        assert email.equals("test@test.com");

        setOff();
    }

    @Test
    void updateUser() throws Exception {
        setUp();

        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.setUpdateString("test");


        // Test every field
        List<String> updateFields = new ArrayList<>();
        updateFields.add("username");
        updateFields.add("password");
        updateFields.add("fullname");
        updateFields.add("email");

        for (String field:
                updateFields) {

            mock.perform(put("/user/update/" + field)).andExpect(status().isUnauthorized());

            var result = mock.perform(put("/user/update/" + field).header("authorization", "Bearer " + userEntity.getToken()).contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(updateRequest))).andExpect(status().isOk()).andReturn();
            String json = result.getResponse().getContentAsString();
            Map<String,String> map = mapper.readValue(json, Map.class);
            String message = map.get("message");

            String uppercasedField = field.substring(0,1).toUpperCase() + field.substring(1).toLowerCase();
            if(uppercasedField.equals("Fullname")) {
                uppercasedField = "Full name";
            }
            assert message.equals(uppercasedField + " updated successfully");
        }

        // Test unexisting field
        var result = mock.perform(put("/user/update/unexistingField").header("authorization", "Bearer " + userEntity.getToken()).contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(updateRequest))).andExpect(status().isBadRequest()).andReturn();
        String json = result.getResponse().getContentAsString();
        Map<String,String> map = mapper.readValue(json, Map.class);

        String message = map.get("message");

        assert message.equals("Invalid Request");

        setOff();
    }
}

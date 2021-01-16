package com.noteit.noteit.users.model;

import lombok.Data;
import lombok.Getter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name="user", schema = "public")
//@Data
@Getter
public class UserEntity {

    public UserEntity() {
    }

    public UserEntity(String email, String token, String full_name, String username, String password) {
        this.email = email;
        this.token = token;
        this.full_name = full_name;
        this.username = username;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Id
    @Column(name="id")
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    @Column(name="email")
    private String email;
    @Column(name="token")
    private String token;
    @Column(name="full_name")
    private String full_name;
    @Column(name="username")
    private String username;

    @Column(name="password")
    private String password;
}

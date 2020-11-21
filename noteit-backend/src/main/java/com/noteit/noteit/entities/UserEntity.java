package com.noteit.noteit.entities;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="user", schema = "public")
@Data
public class UserEntity {

    public UserEntity() {
    }

    public UserEntity(String id, String email, String token, String full_name, String username) {
        this.id = id;
        this.email = email;
        this.token = token;
        this.full_name = full_name;
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    @Id
    @Column(name="id")
    private String id;
    @Column(name="email")
    private String email;
    @Column(name="token")
    private String token;
    @Column(name="full_name")
    private String full_name;
    @Column(name="username")
    private String username;

    public String getId() {
        return id;
    }
}

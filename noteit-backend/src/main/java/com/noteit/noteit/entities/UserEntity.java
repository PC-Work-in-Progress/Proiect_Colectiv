package com.noteit.noteit.entities;

import lombok.Data;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="user", schema = "public")
//@Data
@Getter
public class UserEntity {
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

    public String getUsername() {
        return username;
    }
}

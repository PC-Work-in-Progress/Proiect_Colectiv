package com.noteit.noteit.entities;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name="user", schema = "public")
@Data
public class TestEntity {
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
}

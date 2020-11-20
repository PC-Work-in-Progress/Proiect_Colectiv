package com.noteit.noteit.files.model;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "file")
public class FileDB implements Serializable {

    @Column(name = "id")
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    private String type;

    @Lob
    @Column(name = "uploaded_file")
    private byte[] uploaded_file;

    @Column(name = "approved")
    private Integer approved;

    @Column(name = "date")
    private String date;

    @Column(name = "user_id")
    private String user_id;

    @Column(name = "size")
    private Integer size;

    public FileDB() {

    }
    public FileDB(String name, String type, byte[] uploaded_file, Integer approved, String date, String user_id, Integer size) {
        this.name = name;
        this.type = type;
        this.uploaded_file = uploaded_file;
        this.approved = approved;
        this.date = date;
        this.user_id = user_id;
        this.size = size;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public byte[] getUploaded_file() {
        return uploaded_file;
    }

    public void setUploaded_file(byte[] uploaded_file) {
        this.uploaded_file = uploaded_file;
    }

    public Integer getApproved() {
        return approved;
    }

    public void setApproved(Integer approved) {
        this.approved = approved;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}

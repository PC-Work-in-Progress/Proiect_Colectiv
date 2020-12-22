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


    @Column(name = "date")
    private String date;


    @Column(name = "size")
    private Long size;

    public FileDB() {

    }
    public FileDB(String name, String type, byte[] uploaded_file, String date, Long size) {
        this.name = name;
        this.type = type;
        this.uploaded_file = uploaded_file;
        this.date = date;
        this.size = size;
    }

    public FileDB(String name, String type, byte[] uploadedFile, String date) {
        this.name = name;
        this.type = type;
        this.uploaded_file = uploadedFile;
        this.date = date;
        this.size = (long)uploadedFile.length;
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





    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }
}

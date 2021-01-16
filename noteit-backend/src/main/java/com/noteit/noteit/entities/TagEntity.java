package com.noteit.noteit.entities;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "tag")
@Data
public class TagEntity implements Serializable {
    @Id
    @Column(name="id")
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    @Column(name="name")
    private String name;
    @Column(name="predefined")
    private Integer predefined;

    public TagEntity()
    {

    }

    public TagEntity(String id, String name, Integer predefined) {
        this.id = id;
        this.name = name;
        this.predefined = predefined;
    }

    public TagEntity(String name, Integer predefined) {
        this.name = name;
        this.predefined = predefined;
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
}

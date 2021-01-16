package com.noteit.noteit.rooms.model;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="room", schema = "public")
@Data
public class RoomEntity implements Serializable {
    @Id
    @Column(name="id")
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @Column(name="name")
    private String name;

    @Column(name="owner_id")
    private String ownerId;

    public RoomEntity()
    {

    }

    public RoomEntity(String name, String ownerId)
    {
        this.name = name;
        this.ownerId = ownerId;
    }

    public RoomEntity(String id, String name, String ownerId)
    {
        this.id = id;
        this.name = name;
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        RoomEntity other = (RoomEntity) obj;
        if (false == this.id.equals(other.getId()))
        {
            return false;
        }
        return true;
    }
}

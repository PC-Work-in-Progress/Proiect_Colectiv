package com.noteit.noteit.rooms.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="user_room", schema = "public")
@Data
public class UserRoomEntity implements Serializable {
    @EmbeddedId
    private UserRoomIdEntity<String, String> userRoomId;

    public UserRoomEntity()
    {

    }

    public UserRoomEntity(String userId, String roomId)
    {
        this.userRoomId = new UserRoomIdEntity<>(userId, roomId);
    }

    public UserRoomIdEntity<String, String> getUserRoomId() {
        return userRoomId;
    }
}

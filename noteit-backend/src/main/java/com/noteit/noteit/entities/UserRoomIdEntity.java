package com.noteit.noteit.entities;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class UserRoomIdEntity<F, S> implements Serializable {
    private F userId;
    private S roomId;

    public UserRoomIdEntity()
    {

    }

    public UserRoomIdEntity(F userId, S roomId) {
        this.userId = userId;
        this.roomId = roomId;
    }

    public F getUserId() {
        return userId;
    }

    public void setUserId(F userId) {
        this.userId = userId;
    }

    public S getRoomId() {
        return roomId;
    }

    public void setRoomId(S roomId) {
        this.roomId = roomId;
    }
}

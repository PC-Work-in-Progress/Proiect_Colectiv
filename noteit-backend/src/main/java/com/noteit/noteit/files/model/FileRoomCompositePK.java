package com.noteit.noteit.files.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class FileRoomCompositePK implements Serializable {

    @Column(name = "room_id")
    private String roomId;

    @Column(name = "file_id")
    private String fileId;

    public FileRoomCompositePK(String roomId, String fileId) {
        this.roomId = roomId;
        this.fileId = fileId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileRoomCompositePK that = (FileRoomCompositePK) o;
        return Objects.equals(roomId, that.roomId) &&
                Objects.equals(fileId, that.fileId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomId, fileId);
    }

    public FileRoomCompositePK() {
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
}
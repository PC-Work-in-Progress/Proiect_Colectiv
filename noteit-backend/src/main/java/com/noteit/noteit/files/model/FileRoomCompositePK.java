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

    @Column(name = "approved")
    private Integer approved;

    @Column(name = "views")
    private Integer views;

    @Column(name = "downloads")
    private Integer downloads;

    @Column(name = "user_id")
    private String userId;

    public FileRoomCompositePK(String roomId, String fileId, String userId) {
        this.roomId = roomId;
        this.fileId = fileId;
        this.userId = userId;
        this.approved = 0;
        this.downloads = 0;
        this.views = 0;
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

    public Integer getApproved() {
        return approved;
    }

    public void setApproved(Integer approved) {
        this.approved = approved;
    }

    public Integer getViews() {
        return views;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public Integer getDownloads() {
        return downloads;
    }

    public void setDownloads(Integer downloads) {
        this.downloads = downloads;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
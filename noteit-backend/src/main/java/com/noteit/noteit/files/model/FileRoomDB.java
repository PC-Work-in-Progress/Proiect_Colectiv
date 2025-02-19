package com.noteit.noteit.files.model;

import javax.persistence.*;

@Entity
@Table(name = "file_room")
public class FileRoomDB {

    @EmbeddedId
    private FileRoomCompositePK id;

    public FileRoomDB() {
    }

    public FileRoomDB(FileRoomCompositePK id) {
        this.id = id;
    }

    public FileRoomCompositePK getId() {
        return id;
    }

    public void setId(FileRoomCompositePK id) {
        this.id = id;
    }

    public void Accept() {
    this.id.setApproved(1);
    }

    public boolean isAccepted() {
        return this.id.getApproved()==1;
    }

    public void View() {
        this.id.setViews(this.id.getViews()+1);
    }

    public void Download() {
        this.id.setDownloads(this.id.getDownloads()+1);
    }
}

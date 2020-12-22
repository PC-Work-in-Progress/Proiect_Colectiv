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
}

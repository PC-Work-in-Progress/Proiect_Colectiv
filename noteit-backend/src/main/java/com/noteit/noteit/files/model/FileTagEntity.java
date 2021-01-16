package com.noteit.noteit.files.model;

import javax.persistence.*;
import java.io.Serializable;

/*
    Cross-table between File table and Tag table
    uses a composite primary key defined by FileTagPK
 */
@Entity
@Table(name = "file_tag")
public class FileTagEntity implements Serializable {
   @EmbeddedId
    private FileTagPK id;

    public FileTagEntity() {
    }

    public FileTagEntity(FileTagPK id) {
        this.id = id;
    }

    public FileTagPK getId() {
        return id;
    }

    public void setId(FileTagPK id) {
        this.id = id;
    }
}

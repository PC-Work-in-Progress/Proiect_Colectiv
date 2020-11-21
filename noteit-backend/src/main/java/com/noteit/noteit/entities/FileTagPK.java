package com.noteit.noteit.entities;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/*
    The composite key for Files and tags
    use data from the 2 columns from the table
 */
@Embeddable
public class FileTagPK implements Serializable {
    @Column(name = "file_id")
    private String fileId;

    @Column(name = "tag_id")
    private String tagId;

    public FileTagPK() {
    }

    public FileTagPK(String fileId, String tagId) {
        this.fileId = fileId;
        this.tagId = tagId;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }
}

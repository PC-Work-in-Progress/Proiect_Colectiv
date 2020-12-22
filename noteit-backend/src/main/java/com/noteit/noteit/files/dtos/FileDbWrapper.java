package com.noteit.noteit.files.dtos;


public class FileDbWrapper {
    private String fileName;
    private String fileId;
    private String fileType;
    private Long size;
    private String date;
    private String username;
    private Integer approved;

    public FileDbWrapper(String fileName, String fileId, String fileType, Long size, String date, String username, Integer approved) {
        this.fileName = fileName;
        this.fileId = fileId;
        this.fileType = fileType;
        this.size = size;
        this.date = date;
        this.username = username;
        this.approved = approved;
    }

    public Integer getApproved() {
        return approved;
    }

    public void setApproved(Integer approved) {
        this.approved = approved;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

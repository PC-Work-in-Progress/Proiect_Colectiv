package com.noteit.noteit.files.message;


public class ResponseFile {
    private String name;
    private String url;
    private String type;
    private long size;
    private String data;
    private String username;

    public ResponseFile(String name, String url, String type, long size, String data, String username) {
        this.name = name;
        this.url = url;
        this.type = type;
        this.size = size;
        this.data = data;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
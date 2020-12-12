package com.noteit.noteit.files.message;

public class ResponseContentFile {
    private byte[] content;
    private String nume;

    public ResponseContentFile(byte[] content, String nume) {
        this.content = content;
        this.nume = nume;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }
}

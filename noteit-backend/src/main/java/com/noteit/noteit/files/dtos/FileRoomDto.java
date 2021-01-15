package com.noteit.noteit.files.dtos;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import com.noteit.noteit.entities.TagEntity;
import java.util.List;

public class FileRoomDto implements Comparable<FileRoomDto>{
    private String userName;
    private String roomName;
    private String roomId;
    private String fileId;
    private String fileName;
    private String fileDate;
    private List<String> tags;

    public FileRoomDto(String userName, String roomName, String roomId , String fileId, String fileName, String fileDate, List<String> tags) {
        this.userName = userName;
        this.roomName = roomName;
        this.roomId   = roomId;
        this.fileId   = fileId;
        this.fileName = fileName;
        this.fileDate = fileDate;
        this.tags     = tags;
    }

    public FileRoomDto ()
    {

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileDate() {
        return fileDate;
    }

    public void setFileDate(String fileDate) {
        this.fileDate = fileDate;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    @Override
    public String toString() {
        return "FileRoomDto{" +
                "userName='" + userName + '\'' +
                ", roomName='" + roomName + '\'' +
                ", roomId='" + roomId + '\'' +
                ", fileId='" + fileId + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileDate='" + fileDate + '\'' +
                ", tags=" + tags +
                '}';
    }

    @Override
    public int compareTo(FileRoomDto o) {
        Date currentFileDate = null;
        String[] auxDate = this.getFileDate().split(" ");
        Date oDate = null;
        try {
            currentFileDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(auxDate[0] + " " + auxDate[2]);
            auxDate = o.getFileDate().split(" ");
            oDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(auxDate[0] + " " + auxDate[2]);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int result = currentFileDate.compareTo(oDate);
        if (result > 0)
        {
            result = -1;
        }
        else
        {
            if (result < 0)
            {
                result = 1;
            }
        }
        return result;
    }
}

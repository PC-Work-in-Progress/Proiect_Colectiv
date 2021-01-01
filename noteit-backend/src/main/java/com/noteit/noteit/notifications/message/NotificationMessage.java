package com.noteit.noteit.notifications.message;

public class NotificationMessage {
    private String id;
    private String message;
    private Integer viewed;

    public NotificationMessage(String id, String message, Integer viewed) {
        this.id = id;
        this.message = message;
        this.viewed = viewed;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getViewed() {
        return viewed;
    }

    public void setViewed(Integer viewed) {
        this.viewed = viewed;
    }
}

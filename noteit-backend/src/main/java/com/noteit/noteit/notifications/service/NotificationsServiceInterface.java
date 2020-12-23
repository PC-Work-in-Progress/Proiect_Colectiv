package com.noteit.noteit.notifications.service;

import com.noteit.noteit.notifications.model.Notification;

public interface NotificationsServiceInterface {
    void pushNotification(String toNotifyUserId, String message);

}

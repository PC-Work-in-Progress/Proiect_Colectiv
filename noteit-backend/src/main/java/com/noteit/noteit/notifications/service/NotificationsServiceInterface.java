package com.noteit.noteit.notifications.service;

import com.noteit.noteit.notifications.exceptions.NotificationAlreadyReadException;
import com.noteit.noteit.notifications.message.NotificationMessage;
import com.noteit.noteit.notifications.model.Notification;

import java.util.List;

public interface NotificationsServiceInterface {
    void pushNotification(String toNotifyUserId, String message);

    List<NotificationMessage> getNotifications(String userId);

    Notification readNotification(String id) throws NotificationAlreadyReadException;
}

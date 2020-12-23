package com.noteit.noteit.notifications.service;

import com.noteit.noteit.notifications.model.Notification;
import com.noteit.noteit.notifications.repository.NotificationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationsService implements NotificationsServiceInterface {

    @Autowired
    private NotificationsRepository notificationsRepository;
    @Override
    public void pushNotification(String toNotifyUserId, String message) {
        Notification notification = new Notification(toNotifyUserId, message, 0);
        notificationsRepository.save(notification);
    }
}

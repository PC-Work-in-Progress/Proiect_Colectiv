package com.noteit.noteit.notifications.service;

import com.noteit.noteit.notifications.exceptions.NotificationAlreadyReadException;
import com.noteit.noteit.notifications.message.NotificationMessage;
import com.noteit.noteit.notifications.model.Notification;
import com.noteit.noteit.notifications.repository.NotificationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationsService implements NotificationsServiceInterface {

    @Autowired
    private NotificationsRepository notificationsRepository;
    @Override
    public void pushNotification(String toNotifyUserId, String message) {
        Notification notification = new Notification(toNotifyUserId, message, 0);
        notificationsRepository.save(notification);
    }

    @Override
    public List<NotificationMessage> getNotifications(String userId) {
        return notificationsRepository.findByUserId(userId).stream()
                .map(x->new NotificationMessage(x.getId(), x.getMessage(), x.getViewed()))
                .collect(Collectors.toList());
    }

    @Override
    public Notification readNotification(String id) throws NotificationAlreadyReadException {
        var notificationOptional = notificationsRepository.findById(id);
        if (notificationOptional.isEmpty())
            return null;
        var notif = notificationOptional.get();
        if (notif.getViewed()==1)
            throw new NotificationAlreadyReadException("Conflict with the current state! Notification already seen!");
        notif.setViewed(1);
        return notificationsRepository.save(notif);
    }
}

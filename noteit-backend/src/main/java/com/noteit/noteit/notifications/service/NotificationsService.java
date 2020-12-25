package com.noteit.noteit.notifications.service;

import com.noteit.noteit.notifications.exceptions.NotificationAlreadyReadException;
import com.noteit.noteit.notifications.message.NotificationMessage;
import com.noteit.noteit.notifications.model.Notification;
import com.noteit.noteit.notifications.repository.NotificationsRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class NotificationsService implements NotificationsServiceInterface {

    @Autowired
    private NotificationsRepository notificationsRepository;

    private static final Logger logger = LogManager.getLogger();

    /**
     * function that add a notification that add a notification for a specific user
     * @param toNotifyUserId the id of user that will get the notification
     * @param message the message of notification
     */
    @Override
    public void pushNotification(String toNotifyUserId, String message) {
        logger.info("ENTER pushNotification to : {} with message : {}", toNotifyUserId, message);
        Notification notification = new Notification(toNotifyUserId, message, 0);
        notificationsRepository.save(notification);
        logger.info("EXIT pushNotification with success");
    }

    /**
     * function that returns ALL history notifications of a user, each of them having an notification id,
     * a messsage, and a flag that indicates if a notification was already seen by user until now
     * @param userId the id of user that gets the notifications
     * @return list of notifications
     */
    @Override
    public List<NotificationMessage> getNotifications(String userId) {
        logger.info("ENTER/EXIT getNotifications for user with id : {}", userId);
        return notificationsRepository.findByUserId(userId).stream()
                .map(x->new NotificationMessage(x.getId(), x.getMessage(), x.getViewed()))
                .collect(Collectors.toList());
    }

    /**
     * @param id the id of some notification that will be marked as viewed
     * @return Notification onject that represent the notification modified
     * @throws NotificationAlreadyReadException if the provided id is not a valid id
     */
    @Override
    public Notification readNotification(String id) throws NotificationAlreadyReadException {
        logger.info("ENTER readNotification, setting as viewed for notification with id : {}", id);
        var notificationOptional = notificationsRepository.findById(id);
        if (notificationOptional.isEmpty()) {
         logger.info("EXIT readNotification, invalid id : {}, RETURN : null", id);
            return null;
        }
        var notif = notificationOptional.get();
        if (notif.getViewed()==1) {
            logger.info("EXIT readNotification, notification with id : {} already viewed", id);
            throw new NotificationAlreadyReadException("Conflict with the current state! Notification already seen!");
        }
            notif.setViewed(1);
        logger.info("EXIT readNotification with success");
        return notificationsRepository.save(notif);
    }
}

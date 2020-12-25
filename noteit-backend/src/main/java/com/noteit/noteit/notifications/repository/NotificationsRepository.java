package com.noteit.noteit.notifications.repository;

import com.noteit.noteit.notifications.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationsRepository extends JpaRepository<Notification, String> {
    List<Notification> findByUserId(String userId);
}

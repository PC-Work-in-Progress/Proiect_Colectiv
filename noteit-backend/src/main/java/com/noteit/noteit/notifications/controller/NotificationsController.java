package com.noteit.noteit.notifications.controller;

import com.noteit.noteit.notifications.service.NotificationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@CrossOrigin
@RequestMapping("/api/notifications")
public class NotificationsController {
    @Autowired
    private NotificationsService notificationsService;
}

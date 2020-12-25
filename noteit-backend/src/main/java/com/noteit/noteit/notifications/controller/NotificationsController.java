package com.noteit.noteit.notifications.controller;

import com.noteit.noteit.files.message.ResponseMessage;
import com.noteit.noteit.notifications.exceptions.NotificationAlreadyReadException;
import com.noteit.noteit.notifications.service.NotificationsService;
import com.noteit.noteit.services.UserServiceImplementation;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@CrossOrigin
@RequestMapping("/api/notifications")
public class NotificationsController {
    @Autowired
    private NotificationsService notificationsService;

    @Autowired
    private UserServiceImplementation userService;

    @GetMapping("/GetNotifications")
    public ResponseEntity<?> getNotifications(@RequestHeader Map<String, String> headers) {
        String fullToken = headers.get("authorization");
        if (fullToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessage("Unauthorized action!"));
        }
        var elems = fullToken.split(" ");
        String token = elems[1];

        String userId = null;
        try {
            userId = userService.getUserIdByToken(token);
        } catch (Exception e) {

        }
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessage("Unauthorized action! Invalid token"));
        }

        try {
            return ResponseEntity.ok().body(notificationsService.getNotifications(userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("ReadNotification/{id}")
    public ResponseEntity<?> readNotification(@PathVariable String id, @RequestHeader Map<String, String> headers) {
        String fullToken = headers.get("authorization");
        if (fullToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessage("Unauthorized action!"));
        }
        var elems = fullToken.split(" ");
        String token = elems[1];

        String userId = null;
        try {
            userId = userService.getUserIdByToken(token);
        } catch (Exception e) {

        }
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessage("Unauthorized action! Invalid token"));
        }
        try {
            var a = notificationsService.readNotification(id);
            if (a == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Invalid notification id provided!"));
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("Notification read successfully!"));
            }
        } catch (NotificationAlreadyReadException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseMessage(e.getMessage()));

        }

    }

}

package com.poc.demosof.notificationservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(path = "/push-notification/")
public class PushNotificationController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;


    @PostMapping
    public ResponseEntity<String> send(@AuthenticationPrincipal Jwt principal, @RequestBody String notificationText) {
        simpMessagingTemplate.convertAndSendToUser(principal.getClaims().get("sub").toString(), "/queue/push-notifications",
                notificationText);
        return new ResponseEntity<>("Notification sent successfully", OK);
    }

    @GetMapping
    public ResponseEntity<String> get() {
        return new ResponseEntity<>("You have logged in. Please start ws-queue.html from Intellij in order to have the right port.", OK);
    }
}

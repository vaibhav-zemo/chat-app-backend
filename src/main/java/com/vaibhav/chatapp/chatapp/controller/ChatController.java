package com.vaibhav.chatapp.chatapp.controller;

import com.vaibhav.chatapp.chatapp.model.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.Instant;

@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessage message, Principal principal) {
        message.setSenderId(principal.getName());
        message.setTimestamp(Instant.now());

        if ("GROUP".equalsIgnoreCase(message.getType())) {
            messagingTemplate.convertAndSend(
                    "/topic/rooms/" + message.getChatId(),
                    message
            );
        } else if ("ONE_TO_ONE".equalsIgnoreCase(message.getType())) {
            messagingTemplate.convertAndSendToUser(
                    message.getRecipientId(),
                    "/queue/messages",
                    message
            );
        }
    }
}


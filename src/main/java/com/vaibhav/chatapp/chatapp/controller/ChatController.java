package com.vaibhav.chatapp.chatapp.controller;

import com.vaibhav.chatapp.chatapp.dto.ChatMessageDto;
import com.vaibhav.chatapp.chatapp.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatMessageService chatMessageService;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessageDto message, Principal principal) {
        message.setSenderId(Long.valueOf(principal.getName()));

        ChatMessageDto savedMessage = chatMessageService.saveMessage(message);

        if ("GROUP".equalsIgnoreCase(message.getType())) {
            messagingTemplate.convertAndSend(
                    "/topic/rooms." + message.getChatId(),
                    savedMessage
            );
        } else if ("ONE_TO_ONE".equalsIgnoreCase(message.getType())) {
            messagingTemplate.convertAndSendToUser(
                    String.valueOf(message.getRecipientId()),
                    "/queue/messages",
                    savedMessage
            );
        }
    }
}


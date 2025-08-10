package com.vaibhav.chatapp.chatapp.controller;

import com.vaibhav.chatapp.chatapp.dto.ChatMessageDto;
import com.vaibhav.chatapp.chatapp.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chats")
public class ChatMessageController {

    @Autowired
    private ChatMessageService chatService;

    @GetMapping("/{chatId}/messages")
    public ResponseEntity<Page<ChatMessageDto>> getChatMessages(
            @PathVariable String chatId,
            @PageableDefault(size = 50, sort = "timestamp") Pageable pageable) {

        Page<ChatMessageDto> history = chatService.getChatHistory(chatId, pageable);
        return ResponseEntity.ok(history);
    }
}
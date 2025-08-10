package com.vaibhav.chatapp.chatapp.dto;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;

@Data
@Builder
public class ChatMessageDto {
    private String chatId;
    private Long senderId;
    private Long recipientId;
    private String content;
    private Instant timestamp;
    private String type;
}


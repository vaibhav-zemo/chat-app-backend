package com.vaibhav.chatapp.chatapp.mapper;

import com.vaibhav.chatapp.chatapp.dto.ChatMessageDto;
import com.vaibhav.chatapp.chatapp.model.ChatMessage;
import com.vaibhav.chatapp.chatapp.model.User;
import org.springframework.stereotype.Component;

@Component
public class ChatMessageMapper {

    public ChatMessage toEntity(ChatMessageDto dto, User sender) {
        if (dto == null) return null;
        return ChatMessage.builder()
                .chatId(dto.getChatId())
                .sender(sender)
                .recipientId(dto.getRecipientId())
                .content(dto.getContent())
                .build();
    }

    public ChatMessageDto toDto(ChatMessage entity) {
        if (entity == null) return null;
        return ChatMessageDto.builder()
                .chatId(entity.getChatId())
                .senderId(entity.getSender().getUserId())
                .recipientId(entity.getRecipientId())
                .content(entity.getContent())
                .timestamp(entity.getTimestamp())
                .build();
    }
}

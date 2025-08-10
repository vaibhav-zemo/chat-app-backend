package com.vaibhav.chatapp.chatapp.service;

import com.vaibhav.chatapp.chatapp.dto.ChatMessageDto;
import com.vaibhav.chatapp.chatapp.mapper.ChatMessageMapper;
import com.vaibhav.chatapp.chatapp.model.ChatMessage;
import com.vaibhav.chatapp.chatapp.model.User;
import com.vaibhav.chatapp.chatapp.repository.ChatMessageRepository;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChatMessageService {
    @Autowired
    private ChatMessageRepository messageRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ChatMessageMapper messageMapper;

    @Transactional
    public ChatMessageDto saveMessage(ChatMessageDto messageDto) {
        User sender = userService.findByUserId(messageDto.getSenderId());
        if(sender == null) {
            throw new ResourceNotFoundException("Sender not found with id: " + messageDto.getSenderId());
        }

        return messageMapper.toDto(messageRepository.save(messageMapper.toEntity(messageDto, sender)));
    }

    public Page<ChatMessageDto> getChatHistory(String chatId, Pageable pageable) {
        Page<ChatMessage> messagePage = messageRepository.findByChatIdOrderByTimestampDesc(chatId, pageable);
        return messagePage.map(messageMapper::toDto);
    }
}

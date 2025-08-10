package com.vaibhav.chatapp.chatapp.repository;

import com.vaibhav.chatapp.chatapp.model.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    Page<ChatMessage> findByChatIdOrderByTimestampDesc(String chatId, Pageable pageable);
}

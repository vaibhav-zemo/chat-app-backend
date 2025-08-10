package com.vaibhav.chatapp.chatapp.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "chat_participants")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "participant_seq")
    private Long id;

    // Link to the Chat
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    // Link to the User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String role;

    private Instant joinedAt;
}
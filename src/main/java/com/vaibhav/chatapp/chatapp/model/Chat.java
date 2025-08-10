package com.vaibhav.chatapp.chatapp.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "chats")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "chat_seq")
    @SequenceGenerator(name = "chat_seq", sequenceName = "chat_id_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String type;

    // For group chats
    private String chatName;
    private String chatImageUrl;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    // This relationship links the chat to its messages
    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessage> messages = new ArrayList<>();

    // This relationship links the chat to its participants
    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ChatParticipant> participants = new HashSet<>();
}

package com.huddlespace.backend.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

// This annotation tells MongoDB to create a compound index on senderId and receiverId,
// which is crucial for quickly finding messages between two users.
@Document(collection = "private_messages")
@CompoundIndex(name = "conversation_index", def = "{'senderId': 1, 'receiverId': 1}")
public class PrivateMessage {
    @Id
    private String id;

    // By indexing senderId and receiverId, we optimize lookups.
    @Indexed
    private String senderId;
    
    @Indexed
    private String receiverId;
    
    private String messageText;

    // Indexing the timestamp allows for fast sorting of messages.
    @Indexed(name = "timestamp_index")
    private LocalDateTime timestamp;
    
    private boolean isRead;
    private String senderType; // STUDENT or FACULTY
    private String receiverType; // STUDENT or FACULTY

    public PrivateMessage() {
        this.timestamp = LocalDateTime.now();
        this.isRead = false;
    }

    public PrivateMessage(String senderId, String receiverId, String messageText, String senderType, String receiverType) {
        this();
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageText = messageText;
        this.senderType = senderType;
        this.receiverType = receiverType;
    }

    // --- Getters and Setters (No changes below this line) ---
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getSenderType() {
        return senderType;
    }

    public void setSenderType(String senderType) {
        this.senderType = senderType;
    }

    public String getReceiverType() {
        return receiverType;
    }

    public void setReceiverType(String receiverType) {
        this.receiverType = receiverType;
    }
}
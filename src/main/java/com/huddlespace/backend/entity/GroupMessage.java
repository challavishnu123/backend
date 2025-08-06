package com.huddlespace.backend.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "group_messages")
public class GroupMessage {
    @Id
    private String id;
    private String senderId;
    private String groupId;
    private String messageText;
    private LocalDateTime timestamp;
    private String senderType; // STUDENT or FACULTY
    private String groupType; // SUBJECT, THREAD, GENERAL

    public GroupMessage() {
        this.timestamp = LocalDateTime.now();
    }

    public GroupMessage(String senderId, String groupId, String messageText, String senderType, String groupType) {
        this();
        this.senderId = senderId;
        this.groupId = groupId;
        this.messageText = messageText;
        this.senderType = senderType;
        this.groupType = groupType;
    }

    // Getters and Setters
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

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
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

    public String getSenderType() {
        return senderType;
    }

    public void setSenderType(String senderType) {
        this.senderType = senderType;
    }

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }
}

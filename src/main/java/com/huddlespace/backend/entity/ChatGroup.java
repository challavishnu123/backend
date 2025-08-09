package com.huddlespace.backend.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Document(collection = "chat_groups")
public class ChatGroup {
    @Id
    private String groupId; // MongoDB will auto-generate this value
    private String groupName;
    private String groupType; // SUBJECT, THREAD, GENERAL
    private String createdBy; // Faculty ID who created the group
    private LocalDateTime createdAt;
    private List<String> members; // List of user IDs (students and faculty)
    private String description;
    private boolean isActive;

    public ChatGroup() {
        this.createdAt = LocalDateTime.now();
        this.members = new ArrayList<>();
        this.isActive = true;
    }
    
    /**
     * The correct constructor for creating new groups.
     * It allows MongoDB to generate the groupId automatically.
     */
    public ChatGroup(String groupName, String groupType, String createdBy) {
        this(); // Calls the default constructor to set createdAt, members, etc.
        this.groupName = groupName;
        this.groupType = groupType;
        this.createdBy = createdBy;
    }

    /**
     * This constructor can be kept for other purposes, like mapping from a full DTO if needed.
     */
    public ChatGroup(String groupId, String groupName, String groupType, String createdBy) {
        this();
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupType = groupType;
        this.createdBy = createdBy;
    }

    // --- Getters and Setters ---
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }
}
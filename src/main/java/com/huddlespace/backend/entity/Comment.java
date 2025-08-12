package com.huddlespace.backend.entity;

import java.time.LocalDateTime;

public class Comment {
    private String username;
    private String text;
    private LocalDateTime timestamp;

    public Comment(String username, String text) {
        this.username = username;
        this.text = text;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
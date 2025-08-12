package com.huddlespace.backend.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Document(collection = "image_posts")
public class ImagePost {
    @Id
    private String id;
    private String fileId; // ID from GridFS
    private String username;
    private String description;
    private LocalDateTime timestamp;
    private Set<String> likes = new HashSet<>();
    private List<Comment> comments = new ArrayList<>();

    public ImagePost(String fileId, String username, String description) {
        this.fileId = fileId;
        this.username = username;
        this.description = description;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getFileId() { return fileId; }
    public void setFileId(String fileId) { this.fileId = fileId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public Set<String> getLikes() { return likes; }
    public void setLikes(Set<String> likes) { this.likes = likes; }
    public List<Comment> getComments() { return comments; }
    public void setComments(List<Comment> comments) { this.comments = comments; }
}
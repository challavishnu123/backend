package com.huddlespace.backend.entity;

public class Vote {
    private String studentId;
    private boolean isUpvote; // true for upvote, false for downvote

    public Vote(String studentId, boolean isUpvote) {
        this.studentId = studentId;
        this.isUpvote = isUpvote;
    }

    // Getters and Setters
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public boolean isUpvote() { return isUpvote; }
    public void setUpvote(boolean upvote) { isUpvote = upvote; }
}
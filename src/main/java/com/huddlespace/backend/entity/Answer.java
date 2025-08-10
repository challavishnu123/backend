package com.huddlespace.backend.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "answers")
public class Answer {
    @Id
    private String id;
    private String questionId;
    private String studentId;
    private String answerText;
    private LocalDateTime timestamp;
    private List<Vote> votes = new ArrayList<>();

    public Answer(String questionId, String studentId, String answerText) {
        this.questionId = questionId;
        this.studentId = studentId;
        this.answerText = answerText;
        this.timestamp = LocalDateTime.now();
    }
    
    // Helper methods to calculate vote counts
    public long getUpvotes() {
        return votes.stream().filter(Vote::isUpvote).count();
    }

    public long getDownvotes() {
        return votes.stream().filter(vote -> !vote.isUpvote()).count();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getQuestionId() { return questionId; }
    public void setQuestionId(String questionId) { this.questionId = questionId; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getAnswerText() { return answerText; }
    public void setAnswerText(String answerText) { this.answerText = answerText; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public List<Vote> getVotes() { return votes; }
    public void setVotes(List<Vote> votes) { this.votes = votes; }
}
package com.huddlespace.backend.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;

@Document(collection = "questions")
public class Question {
    @Id
    private String id;
    private String facultyId;
    private String subject;
    private String questionText;
    private LocalDate date;

    public Question(String facultyId, String subject, String questionText) {
        this.facultyId = facultyId;
        this.subject = subject;
        this.questionText = questionText;
        this.date = LocalDate.now();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getFacultyId() { return facultyId; }
    public void setFacultyId(String facultyId) { this.facultyId = facultyId; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
}
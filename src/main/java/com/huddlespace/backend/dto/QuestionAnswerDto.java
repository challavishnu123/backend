package com.huddlespace.backend.dto;

import com.huddlespace.backend.entity.Answer;
import com.huddlespace.backend.entity.Question;
import java.util.List;

public class QuestionAnswerDto {
    private Question question;
    private List<Answer> answers;

    public QuestionAnswerDto(Question question, List<Answer> answers) {
        this.question = question;
        this.answers = answers;
    }

    // Getters and Setters
    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }
    public List<Answer> getAnswers() { return answers; }
    public void setAnswers(List<Answer> answers) { this.answers = answers; }
}
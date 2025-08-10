package com.huddlespace.backend.service;

import com.huddlespace.backend.dto.QuestionAnswerDto;
import com.huddlespace.backend.entity.Answer;
import com.huddlespace.backend.entity.Question;
import java.util.List;

public interface ForumService {
    Question createQuestion(Question question);
    Answer postAnswer(Answer answer);
    Answer voteOnAnswer(String answerId, String studentId, boolean isUpvote);
    List<Question> getAllQuestions();
    
    // This is the correct signature, returning a List of the DTO
    List<QuestionAnswerDto> getQuestionsWithAnswers();
}
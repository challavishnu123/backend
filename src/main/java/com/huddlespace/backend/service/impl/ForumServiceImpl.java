package com.huddlespace.backend.service.impl;

import com.huddlespace.backend.dto.QuestionAnswerDto;
import com.huddlespace.backend.entity.Answer;
import com.huddlespace.backend.entity.Question;
import com.huddlespace.backend.entity.Vote;
import com.huddlespace.backend.repository.AnswerRepository;
import com.huddlespace.backend.repository.QuestionRepository;
import com.huddlespace.backend.service.ForumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ForumServiceImpl implements ForumService {

    @Autowired private QuestionRepository questionRepository;
    @Autowired private AnswerRepository answerRepository;

    @Override
    public Question createQuestion(Question question) {
        return questionRepository.save(question);
    }

    @Override
    public Answer postAnswer(Answer answer) {
        // --- THIS IS THE FIX ---
        // Before saving, check if the student has already submitted an answer for this question.
        if (answerRepository.existsByQuestionIdAndStudentId(answer.getQuestionId(), answer.getStudentId())) {
            // If a record already exists, throw an exception to prevent saving the duplicate.
            throw new RuntimeException("You have already answered this question.");
        }
        // If no existing answer is found, save the new answer.
        return answerRepository.save(answer);
    }

    @Override
    public Answer voteOnAnswer(String answerId, String studentId, boolean isUpvote) {
        Optional<Answer> optionalAnswer = answerRepository.findById(answerId);
        if (optionalAnswer.isEmpty()) {
            throw new RuntimeException("Answer not found");
        }
        Answer answer = optionalAnswer.get();
        
        // Remove any existing vote by this student to allow changing their vote
        answer.getVotes().removeIf(vote -> vote.getStudentId().equals(studentId));
        
        // Add the new vote
        answer.getVotes().add(new Vote(studentId, isUpvote));
        
        return answerRepository.save(answer);
    }

    @Override
    public List<Question> getAllQuestions() {
        return questionRepository.findAllByOrderByDateDesc();
    }
    
    @Override
    public List<QuestionAnswerDto> getQuestionsWithAnswers() {
        List<Question> questions = getAllQuestions();
        return questions.stream()
            .map(question -> new QuestionAnswerDto(
                question,
                answerRepository.findByQuestionIdOrderByTimestampAsc(question.getId())
            ))
            .collect(Collectors.toList());
    }
}
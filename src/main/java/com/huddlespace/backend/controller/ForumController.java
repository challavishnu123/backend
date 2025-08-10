package com.huddlespace.backend.controller;

import com.huddlespace.backend.entity.Answer;
import com.huddlespace.backend.entity.Question;
import com.huddlespace.backend.service.ForumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/forum")
@CrossOrigin(origins = "*")
public class ForumController {

    @Autowired private ForumService forumService;

    @PostMapping("/questions")
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<?> createQuestion(@RequestBody Map<String, String> payload) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String facultyId = auth.getName();
        Question question = new Question(facultyId, payload.get("subject"), payload.get("questionText"));
        return ResponseEntity.ok(forumService.createQuestion(question));
    }

    @GetMapping("/questions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAllQuestionsWithAnswers() {
        return ResponseEntity.ok(forumService.getQuestionsWithAnswers());
    }

    @PostMapping("/answers")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> postAnswer(@RequestBody Map<String, String> payload) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String studentId = auth.getName();
        Answer answer = new Answer(payload.get("questionId"), studentId, payload.get("answerText"));
        return ResponseEntity.ok(forumService.postAnswer(answer));
    }
    
    @PostMapping("/answers/{answerId}/vote")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> voteOnAnswer(@PathVariable String answerId, @RequestBody Map<String, Boolean> payload) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String studentId = auth.getName();
        boolean isUpvote = payload.get("isUpvote");
        return ResponseEntity.ok(forumService.voteOnAnswer(answerId, studentId, isUpvote));
    }
}
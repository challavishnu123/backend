package com.huddlespace.backend.repository;

import com.huddlespace.backend.entity.Answer;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface AnswerRepository extends MongoRepository<Answer, String> {

    /**
     * Finds all answers for a given question, sorted by when they were posted.
     */
    List<Answer> findByQuestionIdOrderByTimestampAsc(String questionId);

    /**
     * Checks if a document exists with a matching questionId and studentId.
     * This is used to prevent a student from answering the same question multiple times.
     * @param questionId The ID of the question to check.
     * @param studentId The ID of the student to check.
     * @return true if an answer from the student for that question already exists, false otherwise.
     */
    boolean existsByQuestionIdAndStudentId(String questionId, String studentId);
} 
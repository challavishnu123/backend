package com.huddlespace.backend.repository;

import com.huddlespace.backend.entity.Question;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface QuestionRepository extends MongoRepository<Question, String> {
    List<Question> findAllByOrderByDateDesc();
}
package com.huddlespace.backend.repository;

import com.huddlespace.backend.entity.Student;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends MongoRepository<Student, String> {
    // ✅ Custom finder
    Student findByRollNumber(String rollNumber);
}

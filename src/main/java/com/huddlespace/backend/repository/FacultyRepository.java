package com.huddlespace.backend.repository;

import com.huddlespace.backend.entity.Faculty;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface FacultyRepository extends MongoRepository<Faculty, String> {
    Optional<Faculty> findByFacultyId(String facultyId);
}
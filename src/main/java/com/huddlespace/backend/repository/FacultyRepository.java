package com.huddlespace.backend.repository;

import com.huddlespace.backend.entity.Faculty;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FacultyRepository extends MongoRepository<Faculty, String> {
    Faculty findByFacultyId(String facultyId);
}

// backend/src/main/java/com/huddlespace/backend/service/impl/FacultyServiceImpl.java
package com.huddlespace.backend.service.impl;

import com.huddlespace.backend.entity.Faculty;
import com.huddlespace.backend.repository.FacultyRepository;
import com.huddlespace.backend.service.FacultyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FacultyServiceImpl implements FacultyService {

    @Autowired
    private FacultyRepository repository;

    @Override
    public Faculty register(Faculty faculty) {
        // Check if faculty already exists before registration
        Optional<Faculty> existing = repository.findByFacultyId(faculty.getFacultyId());
        if (existing.isPresent()) {
            throw new RuntimeException("❌ Faculty already exists with ID: " + faculty.getFacultyId());
        }
        
        // Validate required fields
        if (faculty.getFacultyId() == null || faculty.getFacultyId().trim().isEmpty()) {
            throw new RuntimeException("❌ Faculty ID is required");
        }
        
        if (faculty.getPassword() == null || faculty.getPassword().trim().isEmpty()) {
            throw new RuntimeException("❌ Password is required");
        }
        
        // Save new faculty
        return repository.save(faculty);
    }

    @Override
    public Faculty login(String facultyId, String password) {
        // Find faculty by ID
        Faculty faculty = getFacultyByFacultyId(facultyId);
        
        // Validate password
        if (faculty == null || !faculty.getPassword().equals(password)) {
            throw new RuntimeException("❌ Invalid credentials");
        }
        
        return faculty;
    }

    @Override
    public Faculty updatePassword(String facultyId, String newPassword) {
        // Find faculty
        Faculty faculty = getFacultyByFacultyId(facultyId);
        
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new RuntimeException("❌ New password is required");
        }
        
        // Update password
        faculty.setPassword(newPassword);
        return repository.save(faculty);
    }

    @Override
    public void delete(String facultyId) {
        // Check if faculty exists before deletion
        Faculty faculty = getFacultyByFacultyId(facultyId);
        
        // Delete by faculty ID (which is the primary key)
        if (faculty != null) {
            repository.deleteById(faculty.getFacultyId());
        }
    }

    @Override
    public List<Faculty> getAllFaculties() {
        return repository.findAll();
    }
    
    @Override
    public Faculty getFacultyByFacultyId(String facultyId) {
        if (facultyId == null || facultyId.trim().isEmpty()) {
            return null;
        }
        return repository.findByFacultyId(facultyId)
                .orElse(null);
    }
}
package com.huddlespace.backend.service.impl;

import com.huddlespace.backend.entity.Faculty;
import com.huddlespace.backend.repository.FacultyRepository;
import com.huddlespace.backend.service.FacultyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FacultyServiceImpl implements FacultyService {

    @Autowired
    private FacultyRepository repository;

    @Override
    public Faculty register(Faculty faculty) {
        // Check if faculty already exists before registration
        Faculty existing = repository.findByFacultyId(faculty.getFacultyId());
        if (existing != null) {
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
        // Validate input parameters
        if (facultyId == null || facultyId.trim().isEmpty()) {
            throw new RuntimeException("❌ Faculty ID is required");
        }
        
        if (password == null || password.trim().isEmpty()) {
            throw new RuntimeException("❌ Password is required");
        }
        
        // Find faculty by ID
        Faculty faculty = repository.findByFacultyId(facultyId);
        
        if (faculty == null) {
            // Don't auto-register - throw error instead
            throw new RuntimeException("❌ Faculty not found. Please register first.");
        }
        
        // Validate password
        if (!faculty.getPassword().equals(password)) {
            throw new RuntimeException("❌ Invalid password");
        }
        
        return faculty;
    }

    @Override
    public Faculty updatePassword(String facultyId, String newPassword) {
        // Validate input parameters
        if (facultyId == null || facultyId.trim().isEmpty()) {
            throw new RuntimeException("❌ Faculty ID is required");
        }
        
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new RuntimeException("❌ New password is required");
        }
        
        // Find faculty
        Faculty faculty = repository.findByFacultyId(facultyId);
        if (faculty == null) {
            throw new RuntimeException("❌ Faculty not found with ID: " + facultyId);
        }
        
        // Update password
        faculty.setPassword(newPassword);
        return repository.save(faculty);
    }

    @Override
    public void delete(String facultyId) {
        // Validate input parameter
        if (facultyId == null || facultyId.trim().isEmpty()) {
            throw new RuntimeException("❌ Faculty ID is required");
        }
        
        // Check if faculty exists before deletion
        Faculty faculty = repository.findByFacultyId(facultyId);
        if (faculty == null) {
            throw new RuntimeException("❌ Faculty not found with ID: " + facultyId);
        }
        
        // Delete by faculty ID (which is the primary key)
        repository.deleteById(facultyId);
    }

    @Override
    public List<Faculty> getAllFaculties() {
        return repository.findAll();
    }
    
    @Override
    public Faculty getFacultyByFacultyId(String facultyId) {
        // Validate input parameter
        if (facultyId == null || facultyId.trim().isEmpty()) {
            throw new RuntimeException("❌ Faculty ID is required");
        }
        
        return repository.findByFacultyId(facultyId);
    }

    // Additional utility methods
    public boolean existsByFacultyId(String facultyId) {
        if (facultyId == null || facultyId.trim().isEmpty()) {
            return false;
        }
        return repository.findByFacultyId(facultyId) != null;
    }

    public long getFacultyCount() {
        return repository.count();
    }

    // Validate faculty credentials without throwing exceptions
    public boolean validateCredentials(String facultyId, String password) {
        try {
            Faculty faculty = repository.findByFacultyId(facultyId);
            return faculty != null && faculty.getPassword().equals(password);
        } catch (Exception e) {
            return false;
        }
    }
}
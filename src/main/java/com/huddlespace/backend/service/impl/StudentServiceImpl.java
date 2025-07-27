package com.huddlespace.backend.service.impl;

import com.huddlespace.backend.entity.Student;
import com.huddlespace.backend.repository.StudentRepository;
import com.huddlespace.backend.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentRepository repository;

    @Override
    public Student register(Student student) {
        // Check if student already exists before registration
        Student existing = repository.findByRollNumber(student.getRollNumber());
        if (existing != null) {
            throw new RuntimeException("❌ Student already exists with roll number: " + student.getRollNumber());
        }
        
        // Validate required fields
        if (student.getRollNumber() == null || student.getRollNumber().trim().isEmpty()) {
            throw new RuntimeException("❌ Roll number is required");
        }
        
        if (student.getPassword() == null || student.getPassword().trim().isEmpty()) {
            throw new RuntimeException("❌ Password is required");
        }
        
        // Save new student
        return repository.save(student);
    }

    @Override
    public Student login(String rollNumber, String password) {
        // Validate input parameters
        if (rollNumber == null || rollNumber.trim().isEmpty()) {
            throw new RuntimeException("❌ Roll number is required");
        }
        
        if (password == null || password.trim().isEmpty()) {
            throw new RuntimeException("❌ Password is required");
        }
        
        // Find student by roll number
        Student student = repository.findByRollNumber(rollNumber);
        
        if (student == null) {
            // Don't auto-register - throw error instead
            throw new RuntimeException("❌ Student not found. Please register first.");
        }
        
        // Validate password
        if (!student.getPassword().equals(password)) {
            throw new RuntimeException("❌ Invalid password");
        }
        
        return student;
    }

    @Override
    public Student updatePassword(String rollNumber, String newPassword) {
        // Validate input parameters
        if (rollNumber == null || rollNumber.trim().isEmpty()) {
            throw new RuntimeException("❌ Roll number is required");
        }
        
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new RuntimeException("❌ New password is required");
        }
        
        // Find student
        Student student = repository.findByRollNumber(rollNumber);
        if (student == null) {
            throw new RuntimeException("❌ Student not found with roll number: " + rollNumber);
        }
        
        // Update password
        student.setPassword(newPassword);
        return repository.save(student);
    }

    @Override
    public void delete(String rollNumber) {
        // Validate input parameter
        if (rollNumber == null || rollNumber.trim().isEmpty()) {
            throw new RuntimeException("❌ Roll number is required");
        }
        
        // Check if student exists before deletion
        Student student = repository.findByRollNumber(rollNumber);
        if (student == null) {
            throw new RuntimeException("❌ Student not found with roll number: " + rollNumber);
        }
        
        // Delete by roll number (which is the ID)
        repository.deleteById(rollNumber);
    }

    @Override
    public List<Student> getAllStudents() {
        return repository.findAll();
    }

    @Override
    public Student getStudentByRollNumber(String rollNumber) {
        // Validate input parameter
        if (rollNumber == null || rollNumber.trim().isEmpty()) {
            throw new RuntimeException("❌ Roll number is required");
        }
        
        return repository.findByRollNumber(rollNumber);
    }

    // Additional utility methods
    public boolean existsByRollNumber(String rollNumber) {
        if (rollNumber == null || rollNumber.trim().isEmpty()) {
            return false;
        }
        return repository.findByRollNumber(rollNumber) != null;
    }

    public long getStudentCount() {
        return repository.count();
    }

    // Validate student credentials without throwing exceptions
    public boolean validateCredentials(String rollNumber, String password) {
        try {
            Student student = repository.findByRollNumber(rollNumber);
            return student != null && student.getPassword().equals(password);
        } catch (Exception e) {
            return false;
        }
    }
}
package com.huddlespace.backend.service.impl;

import com.huddlespace.backend.entity.Student;
import com.huddlespace.backend.repository.StudentRepository;
import com.huddlespace.backend.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentRepository repository;

    @Override
    public Student register(Student student) {
        // Check if student already exists before registration
        Optional<Student> existing = repository.findByRollNumber(student.getRollNumber());
        if (existing.isPresent()) {
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
        // Find student by roll number
        Student student = getStudentByRollNumber(rollNumber);
        
        // Validate password
        if (!student.getPassword().equals(password)) {
            throw new RuntimeException("❌ Invalid password");
        }
        
        return student;
    }

    @Override
    public Student updatePassword(String rollNumber, String newPassword) {
        // Find student
        Student student = getStudentByRollNumber(rollNumber);

        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new RuntimeException("❌ New password is required");
        }
        
        // Update password
        student.setPassword(newPassword);
        return repository.save(student);
    }

    @Override
    public void delete(String rollNumber) {
        // Check if student exists before deletion
        Student student = getStudentByRollNumber(rollNumber);
        
        // Delete by roll number (which is the ID)
        repository.deleteById(student.getRollNumber());
    }

    @Override
    public List<Student> getAllStudents() {
        return repository.findAll();
    }

    @Override
    public Student getStudentByRollNumber(String rollNumber) {
        if (rollNumber == null || rollNumber.trim().isEmpty()) {
            throw new RuntimeException("❌ Roll number is required");
        }
        return repository.findByRollNumber(rollNumber)
                .orElseThrow(() -> new RuntimeException("❌ Student not found. Please register first."));
    }
}
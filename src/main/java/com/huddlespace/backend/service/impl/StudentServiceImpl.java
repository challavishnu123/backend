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
        Student existing = repository.findByRollNumber(student.getRollNumber());
        if (existing != null) {
            throw new RuntimeException("❌ Student already exists with roll number: " + student.getRollNumber());
        }
        return repository.save(student);
    }

    @Override
    public Student login(String rollNumber, String password) {
        Student student = repository.findByRollNumber(rollNumber);
        if (student != null) {
            if (student.getPassword().equals(password)) {
                return student;
            } else {
                throw new RuntimeException("❌ Incorrect password");
            }
        } else {
            // ✅ Auto-register if not present
            Student newStudent = new Student();
            newStudent.setRollNumber(rollNumber);
            newStudent.setPassword(password);
            return repository.save(newStudent);
        }
    }

    @Override
    public Student updatePassword(String rollNumber, String newPassword) {
        Student student = repository.findByRollNumber(rollNumber);
        if (student != null) {
            student.setPassword(newPassword);
            return repository.save(student);
        }
        throw new RuntimeException("❌ Student not found");
    }

    @Override
    public void delete(String rollNumber) {
        repository.deleteById(rollNumber);
    }

    @Override
    public List<Student> getAllStudents() {
        return repository.findAll();
    }

    @Override
    public Student getStudentByRollNumber(String rollNumber) {
        return repository.findByRollNumber(rollNumber);
    }
}

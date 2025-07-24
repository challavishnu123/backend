package com.huddlespace.backend.service;

import com.huddlespace.backend.entity.Student;

import java.util.List;

public interface StudentService {
    Student register(Student student);
    Student login(String rollNumber, String password);
    Student updatePassword(String rollNumber, String newPassword);
    void delete(String rollNumber);
    List<Student> getAllStudents();

    // ✅ added
    Student getStudentByRollNumber(String rollNumber);
}

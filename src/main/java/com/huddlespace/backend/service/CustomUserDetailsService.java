package com.huddlespace.backend.service;

import com.huddlespace.backend.entity.Faculty;
import com.huddlespace.backend.entity.Student;
import com.huddlespace.backend.repository.FacultyRepository;
import com.huddlespace.backend.repository.StudentRepository;
import com.huddlespace.backend.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // First try to find as student
        Student student = studentRepository.findByRollNumber(username);
        if (student != null) {
            return new CustomUserDetails(student.getRollNumber(), student.getPassword(), "STUDENT");
        }

        // Then try to find as faculty
        Faculty faculty = facultyRepository.findByFacultyId(username);
        if (faculty != null) {
            return new CustomUserDetails(faculty.getFacultyId(), faculty.getPassword(), "FACULTY");
        }

        throw new UsernameNotFoundException("User not found with username: " + username);
    }

    public UserDetails loadUserByUsernameAndType(String username, String userType) throws UsernameNotFoundException {
        if ("STUDENT".equalsIgnoreCase(userType)) {
            Student student = studentRepository.findByRollNumber(username);
            if (student != null) {
                return new CustomUserDetails(student.getRollNumber(), student.getPassword(), "STUDENT");
            }
        } else if ("FACULTY".equalsIgnoreCase(userType)) {
            Faculty faculty = facultyRepository.findByFacultyId(username);
            if (faculty != null) {
                return new CustomUserDetails(faculty.getFacultyId(), faculty.getPassword(), "FACULTY");
            }
        }

        throw new UsernameNotFoundException("User not found with username: " + username + " and type: " + userType);
    }
}
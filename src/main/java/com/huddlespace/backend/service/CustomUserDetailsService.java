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
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Student> student = studentRepository.findByRollNumber(username);
        if (student.isPresent()) {
            Student s = student.get();
            return new CustomUserDetails(s.getRollNumber(), s.getPassword(), "STUDENT");
        }

        Optional<Faculty> faculty = facultyRepository.findByFacultyId(username);
        if (faculty.isPresent()) {
            Faculty f = faculty.get();
            return new CustomUserDetails(f.getFacultyId(), f.getPassword(), "FACULTY");
        }

        throw new UsernameNotFoundException("User not found with username: " + username);
    }
}
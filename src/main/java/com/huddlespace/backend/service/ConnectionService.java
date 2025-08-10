package com.huddlespace.backend.service;

import com.huddlespace.backend.entity.Faculty;
import com.huddlespace.backend.entity.Student;
import com.huddlespace.backend.repository.FacultyRepository;
import com.huddlespace.backend.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ConnectionService {

    @Autowired private StudentRepository studentRepository;
    @Autowired private FacultyRepository facultyRepository;

    public void sendConnectionRequest(String senderId, String receiverId) {
        Object sender = findUser(senderId);
        Object receiver = findUser(receiverId);

        if (sender instanceof Student s) {
            if (s.getConnections().contains(receiverId)) throw new RuntimeException("You are already connected.");
            if (s.getPendingRequestsSent().contains(receiverId)) throw new RuntimeException("Request already sent.");
            s.getPendingRequestsSent().add(receiverId);
            studentRepository.save(s);
        } else if (sender instanceof Faculty f) {
            if (f.getConnections().contains(receiverId)) throw new RuntimeException("You are already connected.");
            if (f.getPendingRequestsSent().contains(receiverId)) throw new RuntimeException("Request already sent.");
            f.getPendingRequestsSent().add(receiverId);
            facultyRepository.save(f);
        }

        if (receiver instanceof Student s) {
            s.getPendingRequestsReceived().add(senderId);
            studentRepository.save(s);
        } else if (receiver instanceof Faculty f) {
            f.getPendingRequestsReceived().add(senderId);
            facultyRepository.save(f);
        }
    }
    
    public void acceptConnectionRequest(String accepterId, String requesterId) {
        Object accepter = findUser(accepterId);
        Object requester = findUser(requesterId);

        if (accepter instanceof Student s) {
            s.getPendingRequestsReceived().remove(requesterId);
            s.getConnections().add(requesterId);
            studentRepository.save(s);
        } else if (accepter instanceof Faculty f) {
            f.getPendingRequestsReceived().remove(requesterId);
            f.getConnections().add(requesterId);
            facultyRepository.save(f);
        }

        if (requester instanceof Student s) {
            s.getPendingRequestsSent().remove(accepterId);
            s.getConnections().add(accepterId);
            studentRepository.save(s);
        } else if (requester instanceof Faculty f) {
            f.getPendingRequestsSent().remove(accepterId);
            f.getConnections().add(accepterId);
            facultyRepository.save(f);
        }
    }
    
    public void rejectConnectionRequest(String rejecterId, String requesterId) {
        Object rejecter = findUser(rejecterId);
        Object requester = findUser(requesterId);
        
        if (rejecter instanceof Student s) {
            s.getPendingRequestsReceived().remove(requesterId);
            studentRepository.save(s);
        } else if (rejecter instanceof Faculty f) {
            f.getPendingRequestsReceived().remove(requesterId);
            facultyRepository.save(f);
        }
        
        if (requester instanceof Student s) {
            s.getPendingRequestsSent().remove(rejecterId);
            studentRepository.save(s);
        } else if (requester instanceof Faculty f) {
            f.getPendingRequestsSent().remove(rejecterId);
            facultyRepository.save(f);
        }
    }

    public List<String> getPendingRequests(String userId) {
        Object user = findUser(userId);
        if (user instanceof Student s) {
            return new ArrayList<>(s.getPendingRequestsReceived());
        } else if (user instanceof Faculty f) {
            return new ArrayList<>(f.getPendingRequestsReceived());
        }
        return List.of();
    }

    private Object findUser(String userId) {
        Optional<Student> studentOptional = studentRepository.findByRollNumber(userId);
        if (studentOptional.isPresent()) {
            return studentOptional.get();
        }
        
        Optional<Faculty> facultyOptional = facultyRepository.findByFacultyId(userId);
        if (facultyOptional.isPresent()) {
            return facultyOptional.get();
        }
        
        throw new RuntimeException("User not found: " + userId);
    }
}
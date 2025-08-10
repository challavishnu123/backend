package com.huddlespace.backend.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

@Document(collection = "faculties")
public class Faculty {
    @Id
    private String facultyId;
    private String password;

    // --- NEW PROFILE FIELDS ---
    private String name;
    private String department;
    private List<String> subjects;
    private String email;
    private String linkedinUrl;

    // --- CONNECTION FIELDS ---
    private Set<String> connections = new HashSet<>();
    private Set<String> pendingRequestsSent = new HashSet<>();
    private Set<String> pendingRequestsReceived = new HashSet<>();

    // --- GETTERS AND SETTERS ---
    public String getFacultyId() { return facultyId; }
    public void setFacultyId(String facultyId) { this.facultyId = facultyId; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public List<String> getSubjects() { return subjects; }
    public void setSubjects(List<String> subjects) { this.subjects = subjects; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getLinkedinUrl() { return linkedinUrl; }
    public void setLinkedinUrl(String linkedinUrl) { this.linkedinUrl = linkedinUrl; }
    public Set<String> getConnections() { return connections; }
    public void setConnections(Set<String> connections) { this.connections = connections; }
    public Set<String> getPendingRequestsSent() { return pendingRequestsSent; }
    public void setPendingRequestsSent(Set<String> pendingRequestsSent) { this.pendingRequestsSent = pendingRequestsSent; }
    public Set<String> getPendingRequestsReceived() { return pendingRequestsReceived; }
    public void setPendingRequestsReceived(Set<String> pendingRequestsReceived) { this.pendingRequestsReceived = pendingRequestsReceived; }
}
package com.huddlespace.backend.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.HashSet;
import java.util.Set;

@Document(collection = "students")
public class Student {
    @Id
    private String rollNumber;
    private String password;

    // --- NEW PROFILE FIELDS ---
    private String name;
    private String year;
    private String department;
    private String section;
    private String email;
    private String linkedinUrl;
    private String githubUrl;

    // --- CONNECTION FIELDS ---
    private Set<String> connections = new HashSet<>();
    private Set<String> pendingRequestsSent = new HashSet<>();
    private Set<String> pendingRequestsReceived = new HashSet<>();

    // --- GETTERS AND SETTERS ---
    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getLinkedinUrl() { return linkedinUrl; }
    public void setLinkedinUrl(String linkedinUrl) { this.linkedinUrl = linkedinUrl; }
    public String getGithubUrl() { return githubUrl; }
    public void setGithubUrl(String githubUrl) { this.githubUrl = githubUrl; }
    public Set<String> getConnections() { return connections; }
    public void setConnections(Set<String> connections) { this.connections = connections; }
    public Set<String> getPendingRequestsSent() { return pendingRequestsSent; }
    public void setPendingRequestsSent(Set<String> pendingRequestsSent) { this.pendingRequestsSent = pendingRequestsSent; }
    public Set<String> getPendingRequestsReceived() { return pendingRequestsReceived; }
    public void setPendingRequestsReceived(Set<String> pendingRequestsReceived) { this.pendingRequestsReceived = pendingRequestsReceived; }
}
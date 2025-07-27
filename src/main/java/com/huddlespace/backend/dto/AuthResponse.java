package com.huddlespace.backend.dto;

public class AuthResponse {
    private String token;
    private String message;
    private String userType;
    private String username;
    private Object userData;

    public AuthResponse() {}

    public AuthResponse(String token, String message, String userType, String username, Object userData) {
        this.token = token;
        this.message = message;
        this.userType = userType;
        this.username = username;
        this.userData = userData;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Object getUserData() {
        return userData;
    }

    public void setUserData(Object userData) {
        this.userData = userData;
    }
}
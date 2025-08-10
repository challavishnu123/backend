package com.huddlespace.backend.controller;

import com.huddlespace.backend.service.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map; // --- THIS IS THE FIX --- Add this import statement

@RestController
@RequestMapping("/api/connections")
@CrossOrigin(origins = "*")
public class ConnectionController {

    @Autowired
    private ConnectionService connectionService;

    @PostMapping("/request/{receiverId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> sendConnectionRequest(@PathVariable String receiverId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String senderId = auth.getName();
        connectionService.sendConnectionRequest(senderId, receiverId);
        return ResponseEntity.ok().body(Map.of("message", "Connection request sent."));
    }

    @PostMapping("/accept/{requesterId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> acceptConnectionRequest(@PathVariable String requesterId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String accepterId = auth.getName();
        connectionService.acceptConnectionRequest(accepterId, requesterId);
        return ResponseEntity.ok().body(Map.of("message", "Connection request accepted."));
    }

    @PostMapping("/reject/{requesterId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> rejectConnectionRequest(@PathVariable String requesterId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String rejecterId = auth.getName();
        connectionService.rejectConnectionRequest(rejecterId, requesterId);
        return ResponseEntity.ok().body(Map.of("message", "Connection request rejected."));
    }

    @GetMapping("/pending")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getPendingRequests() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName();
        return ResponseEntity.ok(connectionService.getPendingRequests(userId));
    }
}
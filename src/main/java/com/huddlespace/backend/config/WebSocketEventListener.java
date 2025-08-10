package com.huddlespace.backend.config;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import java.util.Map;

@Component
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messagingTemplate;

    public WebSocketEventListener(SimpMessageSendingOperations messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        
        // --- THIS IS THE FIX ---
        // Add a null check to ensure session attributes exist before we try to use them.
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        if (sessionAttributes != null) {
            String username = (String) sessionAttributes.get("username");
            String userType = (String) sessionAttributes.get("userType");
            
            if (username != null) {
                System.out.println("User connected: " + username + " (" + userType + ") - Session: " + sessionId);
            }
        } else {
            System.out.println("WebSocket Connect: Session attributes were not ready for session ID: " + sessionId);
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        
        // --- ADDED FOR SAFETY ---
        // Also add a null check here for disconnection events.
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        if (sessionAttributes != null) {
            String username = (String) sessionAttributes.get("username");
            String userType = (String) sessionAttributes.get("userType");
            
            if (username != null) {
                System.out.println("User disconnected: " + username + " (" + userType + ") - Session: " + sessionId);
            }
        } else {
             System.out.println("WebSocket Disconnect: Session attributes were not available for session ID: " + sessionId);
        }
    }
}
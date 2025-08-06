package com.huddlespace.backend.config;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

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
        
        // Get user information from session attributes
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        String userType = (String) headerAccessor.getSessionAttributes().get("userType");
        
        if (username != null) {
            System.out.println("User connected: " + username + " (" + userType + ") - Session: " + sessionId);
            
            // Optional: Notify other users about user's online status
            // messagingTemplate.convertAndSend("/topic/user-status", 
            //     Map.of("username", username, "status", "online", "userType", userType));
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        
        // Get user information from session attributes
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        String userType = (String) headerAccessor.getSessionAttributes().get("userType");
        
        if (username != null) {
            System.out.println("User disconnected: " + username + " (" + userType + ") - Session: " + sessionId);
            
            // Optional: Notify other users about user's offline status
            // messagingTemplate.convertAndSend("/topic/user-status", 
            //     Map.of("username", username, "status", "offline", "userType", userType));
        }
    }
}
package com.huddlespace.backend.controller;

import com.huddlespace.backend.dto.GroupMessageDto;
import com.huddlespace.backend.dto.PrivateMessageDto;
import com.huddlespace.backend.entity.GroupMessage;
import com.huddlespace.backend.entity.PrivateMessage;
import com.huddlespace.backend.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class MessageController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Handle private messages
     * Destination: /app/private-message
     * Response sent to: /user/{receiverId}/queue/messages
     */
    @MessageMapping("/private-message")
    public void sendPrivateMessage(@Payload PrivateMessageDto messageDto, 
                                  SimpMessageHeaderAccessor headerAccessor) {
        try {
            // Get sender information from WebSocket session
            String authenticatedUser = (String) headerAccessor.getSessionAttributes().get("username");
            String userType = (String) headerAccessor.getSessionAttributes().get("userType");

            // Validate sender authorization
            if (!authenticatedUser.equals(messageDto.getSenderId())) {
                throw new RuntimeException("Unauthorized: Cannot send message as different user");
            }

            // Set sender type from session
            messageDto.setSenderType(userType);

            // Save message to database
            PrivateMessage savedMessage = chatService.sendPrivateMessage(messageDto);

            // Create response DTO with saved message data
            PrivateMessageDto responseDto = new PrivateMessageDto();
            responseDto.setId(savedMessage.getId());
            responseDto.setSenderId(savedMessage.getSenderId());
            responseDto.setReceiverId(savedMessage.getReceiverId());
            responseDto.setMessageText(savedMessage.getMessageText());
            responseDto.setTimestamp(savedMessage.getTimestamp());
            responseDto.setRead(savedMessage.isRead());
            responseDto.setSenderType(savedMessage.getSenderType());
            responseDto.setReceiverType(savedMessage.getReceiverType());
            responseDto.setSenderName(authenticatedUser); // Use username as display name

            // Send message to receiver's private queue
            messagingTemplate.convertAndSendToUser(
                messageDto.getReceiverId(),
                "/queue/messages",
                responseDto
            );

            // Send confirmation back to sender
            messagingTemplate.convertAndSendToUser(
                messageDto.getSenderId(),
                "/queue/messages",
                responseDto
            );

        } catch (Exception e) {
            // Send error message back to sender
            messagingTemplate.convertAndSendToUser(
                messageDto.getSenderId(),
                "/queue/errors",
                "Failed to send message: " + e.getMessage()
            );
        }
    }

    /**
     * Handle group messages
     * Destination: /app/group-message
     * Response sent to: /topic/group/{groupId}
     */
    @MessageMapping("/group-message")
    public void sendGroupMessage(@Payload GroupMessageDto messageDto,
                                SimpMessageHeaderAccessor headerAccessor) {
        try {
            // Get sender information from WebSocket session
            String authenticatedUser = (String) headerAccessor.getSessionAttributes().get("username");
            String userType = (String) headerAccessor.getSessionAttributes().get("userType");

            // Validate sender authorization
            if (!authenticatedUser.equals(messageDto.getSenderId())) {
                throw new RuntimeException("Unauthorized: Cannot send message as different user");
            }

            // Set sender type from session
            messageDto.setSenderType(userType);

            // Save message to database
            GroupMessage savedMessage = chatService.sendGroupMessage(messageDto);

            // Create response DTO with saved message data
            GroupMessageDto responseDto = new GroupMessageDto();
            responseDto.setId(savedMessage.getId());
            responseDto.setSenderId(savedMessage.getSenderId());
            responseDto.setGroupId(savedMessage.getGroupId());
            responseDto.setMessageText(savedMessage.getMessageText());
            responseDto.setTimestamp(savedMessage.getTimestamp());
            responseDto.setSenderType(savedMessage.getSenderType());
            responseDto.setGroupType(savedMessage.getGroupType());
            responseDto.setSenderName(authenticatedUser); // Use username as display name

            // Broadcast message to all group members
            messagingTemplate.convertAndSend(
                "/topic/group/" + messageDto.getGroupId(),
                responseDto
            );

        } catch (Exception e) {
            // Send error message back to sender
            messagingTemplate.convertAndSendToUser(
                messageDto.getSenderId(),
                "/queue/errors",
                "Failed to send group message: " + e.getMessage()
            );
        }
    }

    /**
     * Handle joining a group
     * Destination: /app/join-group/{groupId}
     */
    @MessageMapping("/join-group/{groupId}")
    public void joinGroup(@DestinationVariable String groupId,
                         SimpMessageHeaderAccessor headerAccessor) {
        try {
            String authenticatedUser = (String) headerAccessor.getSessionAttributes().get("username");
            
            // Join the group
            chatService.joinGroup(groupId, authenticatedUser);

            // Send confirmation to user
            messagingTemplate.convertAndSendToUser(
                authenticatedUser,
                "/queue/notifications",
                "Successfully joined group: " + groupId
            );

            // Notify group members about new member
            messagingTemplate.convertAndSend(
                "/topic/group/" + groupId + "/members",
                authenticatedUser + " joined the group"
            );

        } catch (Exception e) {
            String authenticatedUser = (String) headerAccessor.getSessionAttributes().get("username");
            messagingTemplate.convertAndSendToUser(
                authenticatedUser,
                "/queue/errors",
                "Failed to join group: " + e.getMessage()
            );
        }
    }

    /**
     * Handle leaving a group
     * Destination: /app/leave-group/{groupId}
     */
    @MessageMapping("/leave-group/{groupId}")
    public void leaveGroup(@DestinationVariable String groupId,
                          SimpMessageHeaderAccessor headerAccessor) {
        try {
            String authenticatedUser = (String) headerAccessor.getSessionAttributes().get("username");
            
            // Leave the group
            chatService.leaveGroup(groupId, authenticatedUser);

            // Send confirmation to user
            messagingTemplate.convertAndSendToUser(
                authenticatedUser,
                "/queue/notifications",
                "Successfully left group: " + groupId
            );

            // Notify group members about member leaving
            messagingTemplate.convertAndSend(
                "/topic/group/" + groupId + "/members",
                authenticatedUser + " left the group"
            );

        } catch (Exception e) {
            String authenticatedUser = (String) headerAccessor.getSessionAttributes().get("username");
            messagingTemplate.convertAndSendToUser(
                authenticatedUser,
                "/queue/errors",
                "Failed to leave group: " + e.getMessage()
            );
        }
    }

    /**
     * Handle message read receipts
     * Destination: /app/mark-read
     */
    @MessageMapping("/mark-read")
    public void markMessageAsRead(@Payload String messageId,
                                 SimpMessageHeaderAccessor headerAccessor) {
        try {
            String authenticatedUser = (String) headerAccessor.getSessionAttributes().get("username");
            
            // Mark message as read
            chatService.markMessageAsRead(messageId);

            // Send read receipt confirmation
            messagingTemplate.convertAndSendToUser(
                authenticatedUser,
                "/queue/read-receipts",
                "Message marked as read: " + messageId
            );

        } catch (Exception e) {
            String authenticatedUser = (String) headerAccessor.getSessionAttributes().get("username");
            messagingTemplate.convertAndSendToUser(
                authenticatedUser,
                "/queue/errors",
                "Failed to mark message as read: " + e.getMessage()
            );
        }
    }

    /**
     * Handle typing indicators for private chat
     * Destination: /app/typing-private/{receiverId}
     */
    @MessageMapping("/typing-private/{receiverId}")
    public void handlePrivateTyping(@DestinationVariable String receiverId,
                                   @Payload boolean isTyping,
                                   SimpMessageHeaderAccessor headerAccessor) {
        try {
            String authenticatedUser = (String) headerAccessor.getSessionAttributes().get("username");
            
            // Send typing indicator to receiver
            java.util.Map<String, Object> typingData = new java.util.HashMap<>();
            typingData.put("senderId", authenticatedUser);
            typingData.put("isTyping", isTyping);
            typingData.put("chatType", "private");
            
            messagingTemplate.convertAndSendToUser(
                receiverId,
                "/queue/typing",
                typingData
            );

        } catch (Exception e) {
            // Silently handle typing indicator errors
            System.err.println("Failed to send typing indicator: " + e.getMessage());
        }
    }

    /**
     * Handle typing indicators for group chat
     * Destination: /app/typing-group/{groupId}
     */
    @MessageMapping("/typing-group/{groupId}")
    public void handleGroupTyping(@DestinationVariable String groupId,
                                 @Payload boolean isTyping,
                                 SimpMessageHeaderAccessor headerAccessor) {
        try {
            String authenticatedUser = (String) headerAccessor.getSessionAttributes().get("username");
            
            // Send typing indicator to group
            java.util.Map<String, Object> typingData = new java.util.HashMap<>();
            typingData.put("senderId", authenticatedUser);
            typingData.put("isTyping", isTyping);
            typingData.put("chatType", "group");
            
            messagingTemplate.convertAndSend(
                "/topic/group/" + groupId + "/typing",
                typingData
            );

        } catch (Exception e) {
            // Silently handle typing indicator errors
            System.err.println("Failed to send group typing indicator: " + e.getMessage());
        }
    }
}
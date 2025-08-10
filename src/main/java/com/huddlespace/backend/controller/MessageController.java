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

    @MessageMapping("/private-message")
    public void sendPrivateMessage(@Payload PrivateMessageDto messageDto, 
                                  SimpMessageHeaderAccessor headerAccessor) {
        try {
            String authenticatedUser = (String) headerAccessor.getSessionAttributes().get("username");
            String userType = (String) headerAccessor.getSessionAttributes().get("userType");

            if (!authenticatedUser.equals(messageDto.getSenderId())) {
                throw new RuntimeException("Unauthorized: Cannot send message as different user");
            }

            // --- NEW SECURITY CHECK ---
            if (!chatService.areUsersConnected(messageDto.getSenderId(), messageDto.getReceiverId())) {
                throw new RuntimeException("Cannot send message to a user you are not connected with.");
            }

            messageDto.setSenderType(userType);
            PrivateMessage savedMessage = chatService.sendPrivateMessage(messageDto);

            PrivateMessageDto responseDto = new PrivateMessageDto();
            responseDto.setId(savedMessage.getId());
            responseDto.setSenderId(savedMessage.getSenderId());
            responseDto.setReceiverId(savedMessage.getReceiverId());
            responseDto.setMessageText(savedMessage.getMessageText());
            responseDto.setTimestamp(savedMessage.getTimestamp());
            responseDto.setRead(savedMessage.isRead());
            responseDto.setSenderType(savedMessage.getSenderType());
            responseDto.setReceiverType(savedMessage.getReceiverType());
            responseDto.setSenderName(authenticatedUser);

            messagingTemplate.convertAndSendToUser(
                messageDto.getReceiverId(), "/queue/messages", responseDto
            );
            messagingTemplate.convertAndSendToUser(
                messageDto.getSenderId(), "/queue/messages", responseDto
            );

        } catch (Exception e) {
            messagingTemplate.convertAndSendToUser(
                messageDto.getSenderId(), "/queue/errors", "Failed to send message: " + e.getMessage()
            );
        }
    }

    // ... other methods in the file remain unchanged ...
}
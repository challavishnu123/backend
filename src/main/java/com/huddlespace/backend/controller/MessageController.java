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
import java.util.Map;

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

    @MessageMapping("/group-message")
    public void sendGroupMessage(@Payload GroupMessageDto messageDto,
                                SimpMessageHeaderAccessor headerAccessor) {
        try {
            String authenticatedUser = (String) headerAccessor.getSessionAttributes().get("username");
            String userType = (String) headerAccessor.getSessionAttributes().get("userType");

            if (!authenticatedUser.equals(messageDto.getSenderId())) {
                throw new RuntimeException("Unauthorized: Cannot send message as different user");
            }

            messageDto.setSenderType(userType);
            GroupMessage savedMessage = chatService.sendGroupMessage(messageDto);

            GroupMessageDto responseDto = new GroupMessageDto();
            responseDto.setId(savedMessage.getId());
            responseDto.setSenderId(savedMessage.getSenderId());
            responseDto.setGroupId(savedMessage.getGroupId());
            responseDto.setMessageText(savedMessage.getMessageText());
            responseDto.setTimestamp(savedMessage.getTimestamp());
            responseDto.setSenderType(savedMessage.getSenderType());
            responseDto.setGroupType(savedMessage.getGroupType());
            responseDto.setSenderName(authenticatedUser);

            messagingTemplate.convertAndSend(
                "/topic/group/" + messageDto.getGroupId(),
                responseDto
            );

        } catch (Exception e) {
            messagingTemplate.convertAndSendToUser(
                messageDto.getSenderId(),
                "/queue/errors",
                "Failed to send group message: " + e.getMessage()
            );
        }
    }

    @MessageMapping("/share-post")
    public void sharePost(@Payload Map<String, String> payload, SimpMessageHeaderAccessor headerAccessor) {
        try {
            String senderId = (String) headerAccessor.getSessionAttributes().get("username");
            String receiverId = payload.get("receiverId");
            String postId = payload.get("postId");
            String postOwner = payload.get("postOwner");
            String fileId = payload.get("fileId");

            String messageText = String.format("SHARED_POST::%s::%s::%s", postId, postOwner, fileId);
            
            PrivateMessageDto messageDto = new PrivateMessageDto(senderId, receiverId, messageText);
            
            sendPrivateMessage(messageDto, headerAccessor);

        } catch (Exception e) {
            String senderId = (String) headerAccessor.getSessionAttributes().get("username");
            messagingTemplate.convertAndSendToUser(
                senderId, "/queue/errors", "Failed to share post: " + e.getMessage()
            );
        }
    }
}
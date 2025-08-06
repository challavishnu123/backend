// src/main/java/com/huddlespace/backend/controller/ChatController.java
package com.huddlespace.backend.controller;

import com.huddlespace.backend.dto.ChatGroupDto;
import com.huddlespace.backend.entity.ChatGroup;
import com.huddlespace.backend.entity.GroupMessage;
import com.huddlespace.backend.entity.PrivateMessage;
import com.huddlespace.backend.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private ChatService chatService;

    /**
     * Get private messages between two users
     */
    @GetMapping("/private-messages/{otherUserId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('FACULTY')")
    public ResponseEntity<?> getPrivateMessages(@PathVariable String otherUserId,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "50") int size) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String currentUserId = auth.getName();

            List<PrivateMessage> messages = chatService.getPrivateMessagesBetweenUsers(
                currentUserId, otherUserId, page, size);

            return ResponseEntity.ok(Map.of(
                "messages", messages,
                "currentUser", currentUserId,
                "otherUser", otherUserId,
                "page", page,
                "size", size,
                "totalMessages", messages.size()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "error", "Failed to fetch private messages",
                        "message", e.getMessage()
                    ));
        }
    }

    /**
     * Create a new chat group (Faculty only)
     */
    @PostMapping("/groups")
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<?> createGroup(@RequestBody ChatGroupDto groupDto) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String currentUserId = auth.getName();

            // Set creator
            groupDto.setCreatedBy(currentUserId);

            ChatGroup createdGroup = chatService.createGroup(groupDto);

            return ResponseEntity.ok(Map.of(
                "message", "Group created successfully",
                "group", createdGroup
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "error", "Failed to create group",
                        "message", e.getMessage()
                    ));
        }
    }

    /**
     * Get all groups for current user
     */
    @GetMapping("/groups")
    @PreAuthorize("hasRole('STUDENT') or hasRole('FACULTY')")
    public ResponseEntity<?> getUserGroups() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String currentUserId = auth.getName();

            List<ChatGroup> userGroups = chatService.getUserGroups(currentUserId);

            return ResponseEntity.ok(Map.of(
                "groups", userGroups,
                "user", currentUserId,
                "count", userGroups.size()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "error", "Failed to fetch user groups",
                        "message", e.getMessage()
                    ));
        }
    }

    /**
     * Get all available groups
     */
    @GetMapping("/groups/all")
    @PreAuthorize("hasRole('STUDENT') or hasRole('FACULTY')")
    public ResponseEntity<?> getAllGroups() {
        try {
            List<ChatGroup> allGroups = chatService.getAllActiveGroups();

            return ResponseEntity.ok(Map.of(
                "groups", allGroups,
                "count", allGroups.size()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "error", "Failed to fetch all groups",
                        "message", e.getMessage()
                    ));
        }
    }

    /**
     * Get group messages
     */
    @GetMapping("/groups/{groupId}/messages")
    @PreAuthorize("hasRole('STUDENT') or hasRole('FACULTY')")
    public ResponseEntity<?> getGroupMessages(@PathVariable String groupId,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "50") int size) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String currentUserId = auth.getName();

            // Verify user is a member of the group
            ChatGroup group = chatService.getGroupById(groupId);
            if (group == null || !group.getMembers().contains(currentUserId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of(
                            "error", "Access denied",
                            "message", "You are not a member of this group"
                        ));
            }

            List<GroupMessage> messages = chatService.getGroupMessages(groupId, page, size);

            return ResponseEntity.ok(Map.of(
                "messages", messages,
                "groupId", groupId,
                "page", page,
                "size", size,
                "totalMessages", messages.size()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "error", "Failed to fetch group messages",
                        "message", e.getMessage()
                    ));
        }
    }

    /**
     * Join a group
     */
    @PostMapping("/groups/{groupId}/join")
    @PreAuthorize("hasRole('STUDENT') or hasRole('FACULTY')")
    public ResponseEntity<?> joinGroup(@PathVariable String groupId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String currentUserId = auth.getName();

            ChatGroup group = chatService.joinGroup(groupId, currentUserId);

            return ResponseEntity.ok(Map.of(
                "message", "Successfully joined group",
                "group", group,
                "user", currentUserId
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "error", "Failed to join group",
                        "message", e.getMessage()
                    ));
        }
    }

    /**
     * Leave a group
     */
    @PostMapping("/groups/{groupId}/leave")
    @PreAuthorize("hasRole('STUDENT') or hasRole('FACULTY')")
    public ResponseEntity<?> leaveGroup(@PathVariable String groupId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String currentUserId = auth.getName();

            chatService.leaveGroup(groupId, currentUserId);

            return ResponseEntity.ok(Map.of(
                "message", "Successfully left group",
                "groupId", groupId,
                "user", currentUserId
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "error", "Failed to leave group",
                        "message", e.getMessage()
                    ));
        }
    }

    /**
     * Get group details
     */
    @GetMapping("/groups/{groupId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('FACULTY')")
    public ResponseEntity<?> getGroupDetails(@PathVariable String groupId) {
        try {
            ChatGroup group = chatService.getGroupById(groupId);
            
            if (group == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                            "error", "Group not found",
                            "groupId", groupId
                        ));
            }

            return ResponseEntity.ok(Map.of(
                "group", group,
                "memberCount", group.getMembers().size()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "error", "Failed to fetch group details",
                        "message", e.getMessage()
                    ));
        }
    }

    /**
     * Get chat dashboard data for current user
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('STUDENT') or hasRole('FACULTY')")
    public ResponseEntity<?> getChatDashboard() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String currentUserId = auth.getName();

            // Get user's conversations
            List<String> conversations = chatService.getUserConversations(currentUserId);
            
            // Get user's groups
            List<ChatGroup> userGroups = chatService.getUserGroups(currentUserId);
            
            // Get unread message count
            long unreadCount = chatService.getUnreadMessageCount(currentUserId);

            Map<String, Object> dashboard = new HashMap<>();
            dashboard.put("user", currentUserId);
            dashboard.put("conversations", conversations);
            dashboard.put("conversationCount", conversations.size());
            dashboard.put("groups", userGroups);
            dashboard.put("groupCount", userGroups.size());
            dashboard.put("unreadMessageCount", unreadCount);

            return ResponseEntity.ok(Map.of(
                "message", "Dashboard data retrieved successfully",
                "dashboard", dashboard
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "error", "Failed to fetch dashboard data",
                        "message", e.getMessage()
                    ));
        }
    }

    /**
     * Get unread messages for current user
     */
    @GetMapping("/unread-messages")
    @PreAuthorize("hasRole('STUDENT') or hasRole('FACULTY')")
    public ResponseEntity<?> getUnreadMessages() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String currentUserId = auth.getName();

            List<PrivateMessage> unreadMessages = chatService.getUnreadMessages(currentUserId);
            long unreadCount = chatService.getUnreadMessageCount(currentUserId);

            return ResponseEntity.ok(Map.of(
                "unreadMessages", unreadMessages,
                "unreadCount", unreadCount,
                "user", currentUserId
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "error", "Failed to fetch unread messages",
                        "message", e.getMessage()
                    ));
        }
    }

    /**
     * Mark all messages from a specific sender as read
     */
    @PutMapping("/mark-read/{senderId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('FACULTY')")
    public ResponseEntity<?> markMessagesAsRead(@PathVariable String senderId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String currentUserId = auth.getName();

            chatService.markAllMessagesAsRead(senderId, currentUserId);

            return ResponseEntity.ok(Map.of(
                "message", "Messages marked as read",
                "sender", senderId,
                "receiver", currentUserId
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "error", "Failed to mark messages as read",
                        "message", e.getMessage()
                    ));
        }
    }

    /**
     * Get user's conversations (list of users they've chatted with)
     */
    @GetMapping("/conversations")
    @PreAuthorize("hasRole('STUDENT') or hasRole('FACULTY')")
    public ResponseEntity<?> getUserConversations() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String currentUserId = auth.getName();

            List<String> conversations = chatService.getUserConversations(currentUserId);

            return ResponseEntity.ok(Map.of(
                "conversations", conversations,
                "user", currentUserId,
                "count", conversations.size()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "error", "Failed to fetch conversations",
                        "message", e.getMessage()
                    ));
        }
    }
}
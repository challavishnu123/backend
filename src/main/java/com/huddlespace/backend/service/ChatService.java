package com.huddlespace.backend.service;

import com.huddlespace.backend.dto.ChatGroupDto;
import com.huddlespace.backend.dto.GroupMessageDto;
import com.huddlespace.backend.dto.PrivateMessageDto;
import com.huddlespace.backend.entity.ChatGroup;
import com.huddlespace.backend.entity.GroupMessage;
import com.huddlespace.backend.entity.PrivateMessage;
import java.util.List;

public interface ChatService {
    
    // Private Message Operations
    PrivateMessage sendPrivateMessage(PrivateMessageDto messageDto);
    List<PrivateMessage> getPrivateMessagesBetweenUsers(String userId1, String userId2, int page, int size);
    List<PrivateMessage> getUnreadMessages(String userId);
    void markMessageAsRead(String messageId);
    void markAllMessagesAsRead(String senderId, String receiverId);
    
    // Group Message Operations
    GroupMessage sendGroupMessage(GroupMessageDto messageDto);
    List<GroupMessage> getGroupMessages(String groupId, int page, int size);
    
    // Group Management Operations
    ChatGroup createGroup(ChatGroupDto groupDto);
    ChatGroup joinGroup(String groupId, String userId);
    void leaveGroup(String groupId, String userId);
    List<ChatGroup> getUserGroups(String userId);
    List<ChatGroup> getAllActiveGroups();
    ChatGroup getGroupById(String groupId);
    
    // Utility Operations
    List<String> getUserConversations(String userId);
    long getUnreadMessageCount(String userId);
    boolean areUsersConnected(String userId1, String userId2);
}
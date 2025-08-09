package com.huddlespace.backend.service.impl;

import com.huddlespace.backend.dto.ChatGroupDto;
import com.huddlespace.backend.dto.GroupMessageDto;
import com.huddlespace.backend.dto.PrivateMessageDto;
import com.huddlespace.backend.entity.ChatGroup;
import com.huddlespace.backend.entity.GroupMessage;
import com.huddlespace.backend.entity.PrivateMessage;
import com.huddlespace.backend.repository.ChatGroupRepository;
import com.huddlespace.backend.repository.GroupMessageRepository;
import com.huddlespace.backend.repository.PrivateMessageRepository;
import com.huddlespace.backend.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private PrivateMessageRepository privateMessageRepository;

    @Autowired
    private GroupMessageRepository groupMessageRepository;

    @Autowired
    private ChatGroupRepository chatGroupRepository;

    @Override
    public PrivateMessage sendPrivateMessage(PrivateMessageDto messageDto) {
        if (messageDto.getSenderId() == null || messageDto.getReceiverId() == null || 
            messageDto.getMessageText() == null || messageDto.getMessageText().trim().isEmpty()) {
            throw new RuntimeException("Invalid message data");
        }

        PrivateMessage message = new PrivateMessage(
            messageDto.getSenderId(),
            messageDto.getReceiverId(),
            messageDto.getMessageText().trim(),
            messageDto.getSenderType(),
            messageDto.getReceiverType()
        );

        return privateMessageRepository.save(message);
    }

    @Override
    public List<PrivateMessage> getPrivateMessagesBetweenUsers(String userId1, String userId2, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "timestamp"));
        return privateMessageRepository.findMessagesBetweenUsers(userId1, userId2, pageable);
    }

    @Override
    public List<PrivateMessage> getUnreadMessages(String userId) {
        return privateMessageRepository.findByReceiverIdAndIsReadFalse(userId);
    }

    @Override
    public void markMessageAsRead(String messageId) {
        privateMessageRepository.findById(messageId).ifPresent(message -> {
            message.setRead(true);
            privateMessageRepository.save(message);
        });
    }

    @Override
    public void markAllMessagesAsRead(String senderId, String receiverId) {
        List<PrivateMessage> unreadMessages = privateMessageRepository
            .findByReceiverIdAndIsReadFalse(receiverId)
            .stream()
            .filter(msg -> msg.getSenderId().equals(senderId))
            .collect(Collectors.toList());

        unreadMessages.forEach(msg -> msg.setRead(true));
        privateMessageRepository.saveAll(unreadMessages);
    }

    @Override
    public GroupMessage sendGroupMessage(GroupMessageDto messageDto) {
        if (messageDto.getSenderId() == null || messageDto.getGroupId() == null || 
            messageDto.getMessageText() == null || messageDto.getMessageText().trim().isEmpty()) {
            throw new RuntimeException("Invalid group message data");
        }

        ChatGroup group = chatGroupRepository.findByGroupId(messageDto.getGroupId());
        if (group == null || !group.isActive()) {
            throw new RuntimeException("Group not found or inactive");
        }

        if (!group.getMembers().contains(messageDto.getSenderId())) {
            throw new RuntimeException("User is not a member of this group");
        }

        GroupMessage message = new GroupMessage(
            messageDto.getSenderId(),
            messageDto.getGroupId(),
            messageDto.getMessageText().trim(),
            messageDto.getSenderType(),
            group.getGroupType()
        );

        return groupMessageRepository.save(message);
    }

    @Override
    public List<GroupMessage> getGroupMessages(String groupId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "timestamp"));
        // --- THIS IS THE FIX ---
        // Pass the entire Pageable object, not just the Sort object.
        return groupMessageRepository.findByGroupIdOrderByTimestampAsc(groupId, pageable);
    }

    @Override
    public ChatGroup createGroup(ChatGroupDto groupDto) {
        if (groupDto.getGroupName() == null || groupDto.getGroupName().trim().isEmpty() || 
            groupDto.getCreatedBy() == null) {
            throw new RuntimeException("Invalid group data: Group name and creator are required.");
        }

        ChatGroup group = new ChatGroup(
            groupDto.getGroupName(),
            groupDto.getGroupType(),
            groupDto.getCreatedBy()
        );

        group.setDescription(groupDto.getDescription());
        group.getMembers().add(groupDto.getCreatedBy());

        return chatGroupRepository.save(group);
    }

    @Override
    public ChatGroup joinGroup(String groupId, String userId) {
        ChatGroup group = chatGroupRepository.findByGroupId(groupId);
        if (group == null || !group.isActive()) {
            throw new RuntimeException("Group not found or inactive");
        }

        if (!group.getMembers().contains(userId)) {
            group.getMembers().add(userId);
            return chatGroupRepository.save(group);
        }

        return group;
    }

    @Override
    public void leaveGroup(String groupId, String userId) {
        ChatGroup group = chatGroupRepository.findByGroupId(groupId);
        if (group != null) {
            group.getMembers().remove(userId);
            chatGroupRepository.save(group);
        }
    }

    @Override
    public List<ChatGroup> getUserGroups(String userId) {
        return chatGroupRepository.findGroupsByMemberAndIsActiveTrue(userId);
    }

    @Override
    public List<ChatGroup> getAllActiveGroups() {
        return chatGroupRepository.findByIsActiveTrue();
    }

    @Override
    public ChatGroup getGroupById(String groupId) {
        return chatGroupRepository.findByGroupId(groupId);
    }

    @Override
    public List<String> getUserConversations(String userId) {
        List<PrivateMessage> messages = privateMessageRepository.findConversationsForUser(userId);
        
        Set<String> conversationUsers = new HashSet<>();
        for (PrivateMessage message : messages) {
            if (message.getSenderId().equals(userId)) {
                conversationUsers.add(message.getReceiverId());
            } else {
                conversationUsers.add(message.getSenderId());
            }
        }
        
        return conversationUsers.stream().collect(Collectors.toList());
    }

    @Override
    public long getUnreadMessageCount(String userId) {
        return privateMessageRepository.countByReceiverIdAndIsReadFalse(userId);
    }
}
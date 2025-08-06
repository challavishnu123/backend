package com.huddlespace.backend.repository;

import com.huddlespace.backend.entity.ChatGroup;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatGroupRepository extends MongoRepository<ChatGroup, String> {
    
    // Find group by group ID
    ChatGroup findByGroupId(String groupId);
    
    // Find groups by type
    List<ChatGroup> findByGroupTypeAndIsActiveTrue(String groupType);
    
    // Find groups created by a faculty
    List<ChatGroup> findByCreatedByAndIsActiveTrue(String createdBy);
    
    // Find groups where user is a member
    @Query("{ 'members': ?0, 'isActive': true }")
    List<ChatGroup> findGroupsByMemberAndIsActiveTrue(String userId);
    
    // Find all active groups
    List<ChatGroup> findByIsActiveTrue();
    
    // Check if group exists by name and type
    ChatGroup findByGroupNameAndGroupTypeAndIsActiveTrue(String groupName, String groupType);
}
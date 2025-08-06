package com.huddlespace.backend.repository;

import com.huddlespace.backend.entity.GroupMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GroupMessageRepository extends MongoRepository<GroupMessage, String> {
    
    // Find messages by group ID with pagination and sorting
    @Query(value = "{ 'groupId': ?0 }", sort = "{ 'timestamp': -1 }")
    List<GroupMessage> findByGroupIdOrderByTimestampDesc(String groupId, Pageable pageable);
    
    // Find messages by group ID after a specific timestamp
    List<GroupMessage> findByGroupIdAndTimestampAfterOrderByTimestampAsc(String groupId, LocalDateTime timestamp);
    
    // Find messages by sender in a group
    List<GroupMessage> findByGroupIdAndSenderIdOrderByTimestampDesc(String groupId, String senderId, Pageable pageable);
    
    // Find recent messages across all groups for a user
    List<GroupMessage> findBySenderIdOrderByTimestampDesc(String senderId, Pageable pageable);
    
    // Count messages in a group
    long countByGroupId(String groupId);
    
    // Find latest message in a group
    @Query(value = "{ 'groupId': ?0 }", sort = "{ 'timestamp': -1 }")
    List<GroupMessage> findLatestMessageInGroup(String groupId, Pageable pageable);
}
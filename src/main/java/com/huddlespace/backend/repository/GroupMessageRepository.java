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
    
    /**
     * Finds messages for a group, sorted descending by timestamp.
     */
    @Query(value = "{ 'groupId': ?0 }", sort = "{ 'timestamp': -1 }")
    List<GroupMessage> findByGroupIdOrderByTimestampDesc(String groupId, Pageable pageable);
    
    /**
     * --- THIS IS THE FIX ---
     * The method to find messages and sort them in ASCENDING order.
     * Spring Data JPA will automatically create the query from this method name.
     * It correctly takes a Pageable object.
     */
    List<GroupMessage> findByGroupIdOrderByTimestampAsc(String groupId, Pageable pageable);

    /**
     * Finds messages by group ID after a specific timestamp.
     */
    List<GroupMessage> findByGroupIdAndTimestampAfterOrderByTimestampAsc(String groupId, LocalDateTime timestamp);
    
    /**
     * Finds messages by sender in a specific group.
     */
    List<GroupMessage> findByGroupIdAndSenderIdOrderByTimestampDesc(String groupId, String senderId, Pageable pageable);
    
    /**
     * Finds recent messages across all groups for a user.
     */
    List<GroupMessage> findBySenderIdOrderByTimestampDesc(String senderId, Pageable pageable);
    
    /**
     * Counts the total messages in a group.
     */
    long countByGroupId(String groupId);
    
    /**
     * Finds the single latest message in a group.
     */
    @Query(value = "{ 'groupId': ?0 }", sort = "{ 'timestamp': -1 }")
    List<GroupMessage> findLatestMessageInGroup(String groupId, Pageable pageable);
}
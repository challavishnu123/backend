package com.huddlespace.backend.repository;

import com.huddlespace.backend.entity.PrivateMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrivateMessageRepository extends MongoRepository<PrivateMessage, String> {
    
    // Find messages between two users (bidirectional)
    @Query("{ $or: [ " +
           "{ 'senderId': ?0, 'receiverId': ?1 }, " +
           "{ 'senderId': ?1, 'receiverId': ?0 } " +
           "] }")
    List<PrivateMessage> findMessagesBetweenUsers(String userId1, String userId2, Pageable pageable);
    
    // Find all messages for a specific user (sent or received)
    @Query("{ $or: [ { 'senderId': ?0 }, { 'receiverId': ?0 } ] }")
    List<PrivateMessage> findAllMessagesForUser(String userId, Pageable pageable);
    
    // Find unread messages for a receiver
    List<PrivateMessage> findByReceiverIdAndIsReadFalse(String receiverId);
    
    // Count unread messages for a receiver
    long countByReceiverIdAndIsReadFalse(String receiverId);
    
    // Find latest message between two users
    @Query(value = "{ $or: [ " +
           "{ 'senderId': ?0, 'receiverId': ?1 }, " +
           "{ 'senderId': ?1, 'receiverId': ?0 } " +
           "] }", 
           sort = "{ 'timestamp': -1 }")
    List<PrivateMessage> findLatestMessageBetweenUsers(String userId1, String userId2, Pageable pageable);
    
    // Find all conversations for a user (distinct other users)
    @Query("{ $or: [ { 'senderId': ?0 }, { 'receiverId': ?0 } ] }")
    List<PrivateMessage> findConversationsForUser(String userId);
}
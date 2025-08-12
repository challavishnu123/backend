package com.huddlespace.backend.repository;

import com.huddlespace.backend.entity.ImagePost;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ImagePostRepository extends MongoRepository<ImagePost, String> {
    List<ImagePost> findAllByOrderByTimestampDesc();
}
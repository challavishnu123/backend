package com.huddlespace.backend.service;

import com.huddlespace.backend.entity.Comment;
import com.huddlespace.backend.entity.ImagePost;
import com.huddlespace.backend.repository.ImagePostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ImagePostService {

    @Autowired
    private ImagePostRepository imagePostRepository;

    @Autowired
    private GridFsService gridFsService; // Inject GridFsService

    public ImagePost createImagePost(String fileId, String username, String description) {
        ImagePost post = new ImagePost(fileId, username, description);
        return imagePostRepository.save(post);
    }

    public List<ImagePost> getAllPosts() {
        return imagePostRepository.findAllByOrderByTimestampDesc();
    }

    public ImagePost likePost(String postId, String username) {
        ImagePost post = imagePostRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        
        if (post.getLikes().contains(username)) {
            post.getLikes().remove(username); // Unlike
        } else {
            post.getLikes().add(username); // Like
        }
        return imagePostRepository.save(post);
    }

    public ImagePost addComment(String postId, String username, String text) {
        ImagePost post = imagePostRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        
        post.getComments().add(new Comment(username, text));
        return imagePostRepository.save(post);
    }
    
    /**
     * --- THIS IS THE NEW METHOD ---
     * Deletes a post after verifying the user is the owner.
     */
    public void deletePost(String postId, String username) {
        ImagePost post = imagePostRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        
        // Security Check: Ensure the user deleting the post is the one who created it.
        if (!post.getUsername().equals(username)) {
            throw new IllegalStateException("You do not have permission to delete this post.");
        }
        
        // Delete the associated image file from GridFS
        gridFsService.deleteFile(post.getFileId());
        
        // Delete the post metadata from the database
        imagePostRepository.delete(post);
    }
}
package com.huddlespace.backend.controller;

import com.huddlespace.backend.entity.ImagePost;
import com.huddlespace.backend.service.ChatService; // --- IMPORT ChatService ---
import com.huddlespace.backend.service.GridFsService;
import com.huddlespace.backend.service.ImagePostService;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@RestController
@RequestMapping("/api/feed")
@CrossOrigin(origins = "*")
public class ImagePostController {

    @Autowired private ImagePostService imagePostService;
    @Autowired private GridFsService gridFsService;
    @Autowired private ChatService chatService; // --- INJECT ChatService ---

    // ... uploadPost, getAllPosts, getImage, getImageThumbnail, like, comment, and deletePost methods remain the same ...

    @PostMapping("/upload")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> uploadPost(@RequestParam("file") MultipartFile file, @RequestParam("description") String description) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        String fileId = gridFsService.storeFile(file);
        ImagePost post = imagePostService.createImagePost(fileId, username, description);
        return ResponseEntity.ok(post);
    }

    @GetMapping("/posts")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAllPosts() {
        return ResponseEntity.ok(imagePostService.getAllPosts());
    }
    
    @GetMapping("/images/{fileId}")
    public ResponseEntity<?> getImage(@PathVariable String fileId) throws IOException {
        com.mongodb.client.gridfs.model.GridFSFile file = gridFsService.getFile(fileId);
        if (file == null) {
            return ResponseEntity.notFound().build();
        }
        
        InputStreamResource resource = new InputStreamResource(gridFsService.getResource(file).getInputStream());
        
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(file.getMetadata().getString("_contentType")))
                .body(resource);
    }

    @GetMapping("/images/{fileId}/thumbnail")
    public ResponseEntity<?> getImageThumbnail(@PathVariable String fileId) throws IOException {
        com.mongodb.client.gridfs.model.GridFSFile file = gridFsService.getFile(fileId);
        if (file == null) {
            return ResponseEntity.notFound().build();
        }

        InputStream dbStream = gridFsService.getResource(file).getInputStream();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        Thumbnails.of(dbStream)
                .width(600)
                .outputQuality(0.85)
                .toOutputStream(outputStream);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        InputStreamResource resource = new InputStreamResource(inputStream);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }

    @PostMapping("/posts/{postId}/like")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> likePost(@PathVariable String postId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return ResponseEntity.ok(imagePostService.likePost(postId, username));
    }

    @PostMapping("/posts/{postId}/comment")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> addComment(@PathVariable String postId, @RequestBody Map<String, String> payload) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        String text = payload.get("text");
        return ResponseEntity.ok(imagePostService.addComment(postId, username, text));
    }
    
    @DeleteMapping("/posts/{postId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deletePost(@PathVariable String postId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        try {
            imagePostService.deletePost(postId, username);
            return ResponseEntity.ok(Map.of("message", "Post deleted successfully."));
        } catch (Exception e) {
            return ResponseEntity.status(403).body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * --- THIS IS THE NEW ENDPOINT ---
     * Handles the HTTP request to share a post with a friend.
     */
    @PostMapping("/share")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> sharePost(@RequestBody Map<String, String> payload) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String senderId = auth.getName();
        
        String receiverId = payload.get("receiverId");
        String postId = payload.get("postId");
        String postOwner = payload.get("postOwner");
        String fileId = payload.get("fileId");

        try {
            chatService.sharePostAsMessage(senderId, receiverId, postId, postOwner, fileId);
            return ResponseEntity.ok(Map.of("message", "Post shared successfully."));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("message", e.getMessage()));
        }
    }
}
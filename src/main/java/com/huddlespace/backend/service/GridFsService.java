package com.huddlespace.backend.service;

import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Service
public class GridFsService {

    private static final Logger logger = LoggerFactory.getLogger(GridFsService.class);

    @Autowired
    private GridFsTemplate gridFsTemplate;

    public String storeFile(MultipartFile file) throws IOException {
        ObjectId id = gridFsTemplate.store(file.getInputStream(), file.getOriginalFilename(), file.getContentType());
        return id.toHexString();
    }

    public GridFSFile getFile(String fileId) {
        logger.info("Attempting to find file with ID: {}", fileId);
        try {
            return gridFsTemplate.findOne(new Query(Criteria.where("_id").is(new ObjectId(fileId))));
        } catch (IllegalArgumentException e) {
            logger.error("Invalid fileId format: {}. It must be a 24-character hex string.", fileId);
            return null;
        }
    }
    
    public GridFsResource getResource(GridFSFile file) {
        return gridFsTemplate.getResource(file);
    }

    /**
     * --- THIS IS THE NEW METHOD ---
     * Deletes a file from GridFS using its fileId.
     */
    public void deleteFile(String fileId) {
        logger.info("Attempting to delete file with ID: {}", fileId);
        try {
            gridFsTemplate.delete(new Query(Criteria.where("_id").is(new ObjectId(fileId))));
        } catch (IllegalArgumentException e) {
            logger.error("Could not delete file. Invalid fileId format: {}", fileId);
        }
    }
}
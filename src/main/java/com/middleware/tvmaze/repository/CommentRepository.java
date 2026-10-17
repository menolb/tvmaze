package com.middleware.tvmaze.repository;

import com.middleware.tvmaze.model.CommentDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface CommentRepository extends MongoRepository<CommentDocument, String> {
    List<CommentDocument> findByShowId(Long showId);
}
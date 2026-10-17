package com.middleware.tvmaze.repository;

import com.middleware.tvmaze.model.ShowCacheDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ShowCacheRepository extends MongoRepository<ShowCacheDocument, Long> {
}
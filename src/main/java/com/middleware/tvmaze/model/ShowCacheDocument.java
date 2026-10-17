package com.middleware.tvmaze.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Map;

@Document(collection = "shows_cache")
public class ShowCacheDocument {
    @Id
    private Long id;
    private Map<String, Object> fullShowData;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Map<String, Object> getFullShowData() { return fullShowData; }
    public void setFullShowData(Map<String, Object> fullShowData) { this.fullShowData = fullShowData; }
}
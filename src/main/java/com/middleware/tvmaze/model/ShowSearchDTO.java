package com.middleware.tvmaze.model;

import java.util.List;

public class ShowSearchDTO {
    private Long id;
    private String name;
    private String channel;
    private String summary;
    private List<String> genres;
    private List<CommentDocument> comments;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public List<String> getGenres() { return genres; }
    public void setGenres(List<String> genres) { this.genres = genres; }
    public List<CommentDocument> getComments() { return comments; }
    public void setComments(List<CommentDocument> comments) { this.comments = comments; }
}
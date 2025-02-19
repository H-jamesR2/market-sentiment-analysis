package com.example.ingestion.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RedditPost implements RedditContent {
    private static final ObjectMapper objectMapper = new ObjectMapper(); // Static Jackson Mapper

    private final String id;
    private final String content;
    private final long timestamp;
    private final String title;
    private final String url;
    private final int upvotes;
    private final String subreddit;

    public RedditPost(String id, String content, long timestamp, String title, String url, int upvotes, String subreddit) {
        this.id = id;
        this.content = content;
        this.timestamp = timestamp;
        this.title = title;
        this.url = url;
        this.upvotes = upvotes;
        this.subreddit = subreddit;
    }

    @Override
    public String getId() { return id; }
    @Override
    public String getContent() { return content; }
    @Override
    public long getTimestamp() { return timestamp; }
    @Override
    public String getType() { return "post"; }

    public String getTitle() { return title; }
    public String getUrl() { return url; }
    public int getUpvotes() { return upvotes; }
    public String getSubreddit() { return subreddit; }

    // Convert object to JSON string
    public String toJson() {
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert RedditPost to JSON", e);
        }
    }

    @Override
    public String toString() {
        return "RedditPost{title='" + title + "', url='" + url + "', upvotes=" + upvotes + ", subreddit='" + subreddit + "'}";
    }
}

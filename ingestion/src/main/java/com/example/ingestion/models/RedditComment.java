package com.example.ingestion.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RedditComment implements RedditContent {
    private static final ObjectMapper objectMapper = new ObjectMapper(); // Static Jackson Mapper

    private final String id;
    private final String postId;  // Now correctly storing the post ID
    private final String content;
    private final String subreddit;
    private final long timestamp;
    private final int upvotes;

    public RedditComment(String id, String postId, String content, String subreddit, long timestamp, int upvotes) {
        this.id = id;
        this.postId = postId;
        this.content = content;
        this.subreddit = subreddit;
        this.timestamp = timestamp;
        this.upvotes = upvotes;
    }

    @Override
    public String getId() { return id; }
    @Override
    public String getContent() { return content; }
    @Override
    public long getTimestamp() { return timestamp; }
    @Override
    public String getType() { return "comment"; }

    public String getPostId() { return postId; }
    public String getSubreddit() { return subreddit; }
    public int getUpvotes() { return upvotes; }

    @Override
    public String toString() {
        return "RedditComment{id='" + id + "', postId='" + postId + "', content='" + content + "', subreddit='" + subreddit + "'}";
    }

    // Convert object to JSON string
    public String toJson() {
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert RedditComment to JSON", e);
        }
    }
}

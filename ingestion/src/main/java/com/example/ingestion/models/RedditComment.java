package com.example.ingestion.models;

public class RedditComment implements RedditContent {
    private final String id;
    private final String content;
    private final long timestamp;
    private final String parentId;
    private final String subreddit;

    public RedditComment(String id, String content, long timestamp, String parentId, String subreddit) {
        this.id = id;
        this.content = content;
        this.timestamp = timestamp;
        this.parentId = parentId;
        this.subreddit = subreddit;
    }

    @Override
    public String getId() { return id; }
    @Override
    public String getContent() { return content; }
    @Override
    public long getTimestamp() { return timestamp; }
    @Override
    public String getType() { return "comment"; }

    public String getParentId() { return parentId; }
    public String getSubreddit() { return subreddit; }

    @Override
    public String toString() {
        return "RedditComment{id='" + id + "', content='" + content + "', subreddit='" + subreddit + "'}";
    }
}

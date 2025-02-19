package com.example.ingestion.models;

public interface RedditContent {
    String getId();
    String getContent();
    long getTimestamp();
    String getType();
}
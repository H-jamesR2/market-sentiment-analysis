package com.example.ingestion.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedditConfig {
    @Value("${reddit.client.id}")
    private String clientId;

    @Value("${reddit.client.secret}")
    private String clientSecret;

    @Value("${reddit.user.agent}")
    private String userAgent;

    public String getClientId() { return clientId; }
    public String getClientSecret() { return clientSecret; }
    public String getUserAgent() { return userAgent; }
}


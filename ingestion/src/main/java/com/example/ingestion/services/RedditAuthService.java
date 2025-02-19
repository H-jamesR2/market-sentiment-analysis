package com.example.ingestion.services;

import com.example.ingestion.config.RedditConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RedditAuthService {
    private final RestTemplate restTemplate;
    private final RedditConfig redditConfig;

    @Autowired
    public RedditAuthService(RestTemplate restTemplate, RedditConfig redditConfig) {
        this.restTemplate = restTemplate;
        this.redditConfig = redditConfig;
    }

    public String getAccessToken() {
        String authUrl = "https://www.reddit.com/api/v1/access_token";
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(redditConfig.getClientId(), redditConfig.getClientSecret());
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> request = new HttpEntity<>("grant_type=client_credentials", headers);
        ResponseEntity<String> response = restTemplate.exchange(authUrl, HttpMethod.POST, request, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to get access token: " + response.getBody());
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            return jsonNode.get("access_token").asText();
        } catch (Exception e) {
            throw new RuntimeException("Error parsing Reddit auth response: " + response.getBody(), e);
        }
    }

    /*
    public String getAccessToken() {
        String authUrl = "https://www.reddit.com/api/v1/access_token";
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(redditConfig.getClientId(), redditConfig.getClientSecret());
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> request = new HttpEntity<>("grant_type=client_credentials", headers);
        ResponseEntity<String> response = restTemplate.exchange(authUrl, HttpMethod.POST, request, String.class);

        System.out.println("Reddit Auth Response: " + response.getBody());

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to get access token: " + response.getBody());
        }


        return response.getBody().split("\"access_token\":\"")[1].split("\"")[0];
    } */
}

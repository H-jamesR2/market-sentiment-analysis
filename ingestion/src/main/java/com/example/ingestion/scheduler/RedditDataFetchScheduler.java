package com.example.ingestion.scheduler;

import com.example.ingestion.config.RedditConfig;
import com.example.ingestion.models.RedditPost;
import com.example.ingestion.parsers.RedditParser;
import com.example.ingestion.producers.RedditProducer;
import com.example.ingestion.services.RedditAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class RedditDataFetchScheduler extends AbstractDataFetchScheduler {
    private static final Logger logger = LoggerFactory.getLogger(RedditDataFetchScheduler.class);
    //private static final String REDDIT_API_URL = "https://www.reddit.com/r/all/top.json?limit=5";

    private static final String REDDIT_API_URL = "https://oauth.reddit.com/r/popular/top.json?limit=10";

    private final RestTemplate restTemplate;
    private final RedditParser redditParser;
    private final RedditProducer redditProducer;
    private final RedditAuthService redditAuthService;
    private final RedditConfig redditConfig;

    @Autowired
    public RedditDataFetchScheduler(
            RestTemplate restTemplate,
            RedditParser redditParser,
            RedditProducer redditProducer,
            RedditAuthService redditAuthService,
            RedditConfig redditConfig) {
        this.restTemplate = restTemplate;
        this.redditParser = redditParser;
        this.redditProducer = redditProducer;
        this.redditAuthService = redditAuthService;
        this.redditConfig = redditConfig;

        logger.info("RedditDataFetchScheduler initialized!"); // Add this log
    }

    @Override
    public void fetchData() {
        try {
            logger.info("Fetching Reddit data from API...");

            String accessToken = redditAuthService.getAccessToken();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            headers.set("User-Agent", redditConfig.getUserAgent());

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    REDDIT_API_URL,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            System.out.println("Reddit Data: " + response.getBody());
            if (response.getBody() != null) {
                List<RedditPost> posts = redditParser.parseRedditPosts(response.getBody());
                posts.forEach(redditProducer::sendPost);
            }
        } catch (Exception e) {
            logger.error("Failed to fetch Reddit data", e);
        }
    }
}




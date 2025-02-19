package com.example.ingestion.fetcher;

import com.example.ingestion.config.RedditConfig;
import com.example.ingestion.models.RedditPost;
import com.example.ingestion.parsers.RedditParser;
import com.example.ingestion.messaging.KafkaProducerService;
import com.example.ingestion.services.RedditAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.List;

/*
Mostly news / genPop-media-slop
    https://oauth.reddit.com/r/popular/top.json?limit=10
WSB_top50 / week @ query. has text:
    https://oauth.reddit.com/r/wallstreetbets/top.json?t=week&limit=50
*/

@Component
public class RedditDataFetcher implements DataFetcher {
    private static final Logger logger = LoggerFactory.getLogger(RedditDataFetcher.class);
    //private static final String REDDIT_API_URL = "https://oauth.reddit.com/r/popular/top.json?limit=10";
    private static final String REDDIT_API_URL = "https://oauth.reddit.com/r/wallstreetbets/top.json?t=week&limit=50";



    private final RestTemplate restTemplate;
    private final RedditParser redditParser;
    private final KafkaProducerService kafkaProducerService;
    private final RedditAuthService redditAuthService;
    private final RedditConfig redditConfig;

    @Autowired
    public RedditDataFetcher(
            RestTemplate restTemplate,
            RedditParser redditParser,
            KafkaProducerService kafkaProducerService,
            RedditAuthService redditAuthService,
            RedditConfig redditConfig) {
        this.restTemplate = restTemplate;
        this.redditParser = redditParser;
        this.kafkaProducerService = kafkaProducerService;
        this.redditAuthService = redditAuthService;
        this.redditConfig = redditConfig;
    }

    @Override
    public void fetchData() {
        try {
            logger.info("Fetching Reddit data...");

            String accessToken = redditAuthService.getAccessToken();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            headers.set("User-Agent", redditConfig.getUserAgent());

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(REDDIT_API_URL, HttpMethod.GET, entity, String.class);

            if (response.getBody() != null) {
                List<RedditPost> posts = redditParser.parseRedditPosts(response.getBody());
                posts.forEach(post -> kafkaProducerService.sendMessage(
                        "reddit_posts",
                        post.getId(),
                        post.toJson()
                ));
            }
        } catch (Exception e) {
            logger.error("Failed to fetch Reddit data", e);
        }
    }
}

package com.example.ingestion.fetcher;

import com.example.ingestion.config.RedditConfig;
import com.example.ingestion.models.RedditPost;
import com.example.ingestion.models.RedditComment;
import com.example.ingestion.parsers.RedditParser;
import com.example.ingestion.messaging.KafkaProducerService;
import com.example.ingestion.services.RedditAuthService;
import com.example.ingestion.services.RedisDeduplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private static final String REDDIT_POSTS_URL = "https://oauth.reddit.com/r/wallstreetbets/top.json?t=week&limit=25";
    private static final String REDDIT_COMMENTS_URL_TEMPLATE = "https://oauth.reddit.com/r/wallstreetbets/comments/%s.json?limit=20";

    private final RestTemplate restTemplate;
    private final RedditParser redditParser;
    private final KafkaProducerService kafkaProducerService;
    private final RedisDeduplicationService redisDeduplicationService;
    private final RedditAuthService redditAuthService;
    private final RedditConfig redditConfig;

    @Autowired
    public RedditDataFetcher(
            RestTemplate restTemplate,
            RedditParser redditParser,
            KafkaProducerService kafkaProducerService,
            RedisDeduplicationService redisDeduplicationService,
            RedditAuthService redditAuthService,
            RedditConfig redditConfig) {
        this.restTemplate = restTemplate;
        this.redditParser = redditParser;
        this.kafkaProducerService = kafkaProducerService;
        this.redisDeduplicationService = redisDeduplicationService;
        this.redditAuthService = redditAuthService;
        this.redditConfig = redditConfig;
    }

    @Override
    public void fetchData() {
        try {
            logger.info("Fetching Reddit posts...");
            String accessToken = redditAuthService.getAccessToken();
            HttpHeaders headers = createHeaders(accessToken);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(REDDIT_POSTS_URL, HttpMethod.GET, entity, String.class);

            if (response.getBody() != null) {
                List<RedditPost> posts = redditParser.parseRedditPosts(response.getBody());
                for (RedditPost post : posts) {
                    processRedditPost(post);
                    fetchRedditComments(post.getId(), headers);
                }
            }
        } catch (Exception e) {
            logger.error("Failed to fetch Reddit data", e);
        }
    }

    private void processRedditPost(RedditPost post) {
        Map<String, String> postData = new HashMap<>();
        postData.put("title", post.getTitle());
        postData.put("content", post.getContent());
        postData.put("timestamp", String.valueOf(post.getTimestamp()));
        postData.put("url", post.getUrl());
        postData.put("upvotes", String.valueOf(post.getUpvotes()));

        boolean isDuplicate = redisDeduplicationService.isDuplicateOrUnchanged(post.getId(), postData, "posts");

        if (!isDuplicate) {
            kafkaProducerService.sendMessage("reddit_posts", post.getId(), post.toJson());
            logger.info("Sent new/updated post to Kafka: {}", post.getId());
        } else {
            logger.info("Skipping duplicate post: {}", post.getId());
        }
    }


    private void fetchRedditComments(String postId, HttpHeaders headers) {
        try {
            String url = String.format(REDDIT_COMMENTS_URL_TEMPLATE, postId);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            if (response.getBody() != null) {
                List<RedditComment> comments = redditParser.parseRedditComments(response.getBody());
                for (RedditComment comment : comments) {
                    processRedditComment(comment);
                }
            }
        } catch (Exception e) {
            logger.error("Failed to fetch comments for post: " + postId, e);
        }
    }

    private void processRedditComment(RedditComment comment) {
        Map<String, String> commentData = new HashMap<>();
        commentData.put("content", comment.getContent());
        commentData.put("timestamp", String.valueOf(comment.getTimestamp()));
        commentData.put("upvotes", String.valueOf(comment.getUpvotes()));

        boolean isDuplicate = redisDeduplicationService.isDuplicateOrUnchanged(comment.getId(), commentData, "comments");

        if (!isDuplicate) {
            kafkaProducerService.sendMessage("reddit_comments", comment.getId(), comment.toJson());
            logger.info("Sent new/updated comment to Kafka: {}", comment.getId());
        } else {
            logger.info("Skipping duplicate comment: {}", comment.getId());
        }
    }

    private HttpHeaders createHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("User-Agent", redditConfig.getUserAgent());
        return headers;
    }
}

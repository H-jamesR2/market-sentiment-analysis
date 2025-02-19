package com.example.ingestion.fetcher;

import com.example.ingestion.models.RedditComment;
import com.example.ingestion.messaging.KafkaProducerService;
import com.example.ingestion.parsers.RedditParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@Component
public class RedditCommentFetcher {
    private static final Logger logger = LoggerFactory.getLogger(RedditCommentFetcher.class);
    private final RestTemplate restTemplate;
    private final RedditParser redditParser;
    private final KafkaProducerService kafkaProducerService;

    public RedditCommentFetcher(RestTemplate restTemplate, RedditParser redditParser, KafkaProducerService kafkaProducerService) {
        this.restTemplate = restTemplate;
        this.redditParser = redditParser;
        this.kafkaProducerService = kafkaProducerService;
    }

    public void fetchCommentsForPost(String postId) {
        try {
            String url = "https://oauth.reddit.com/comments/" + postId + ".json?limit=5";
            String response = restTemplate.getForObject(url, String.class);

            if (response != null) {
                List<RedditComment> comments = redditParser.parseRedditComments(response);
                comments.forEach(comment ->
                        kafkaProducerService.sendMessage("reddit_comments", comment.getId(), comment.toJson())
                );
            }
        } catch (Exception e) {
            logger.error("Failed to fetch comments for post {}", postId, e);
        }
    }
}



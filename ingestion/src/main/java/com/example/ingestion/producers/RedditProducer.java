package com.example.ingestion.producers;

import com.example.ingestion.messaging.KafkaProducerService;
import com.example.ingestion.models.RedditPost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.TimeUnit;

@Component
public class RedditProducer {
    private static final Logger logger = LoggerFactory.getLogger(RedditProducer.class);
    private static final String TOPIC = "reddit_posts";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final KafkaProducerService kafkaProducerService;

    @Autowired
    public RedditProducer(KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
    }

    public void sendPost(RedditPost post) {
        int maxRetries = 3;
        int attempt = 0;

        while (attempt < maxRetries) {
            try {
                String jsonPayload = convertPostToJson(post);
                kafkaProducerService.sendMessage(TOPIC, post.getId(), jsonPayload); // pass topic, key, payload
                logger.info("Sent post to Kafka: {}", jsonPayload);
                return;
            } catch (Exception e) {
                attempt++;
                logger.error("Failed to send Reddit post to Kafka (attempt {}/{}). Retrying...", attempt, maxRetries, e);
                try {
                    TimeUnit.SECONDS.sleep(2); // Simple backoff strategy
                } catch (InterruptedException ignored) {}
            }
        }

        logger.error("Giving up on sending Reddit post to Kafka after {} attempts", maxRetries);
    }


    private String convertPostToJson(RedditPost post) {
        try {
            return objectMapper.writeValueAsString(post);
        } catch (Exception e) {
            logger.error("Error converting RedditPost to JSON", e);
            return "{}"; // Return empty JSON to avoid Kafka failures
        }
    }
}


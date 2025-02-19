package com.example.ingestion.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import org.springframework.kafka.support.SendResult;
import java.util.concurrent.CompletableFuture;

import jakarta.annotation.PostConstruct;


@Service
public class KafkaProducerService {
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostConstruct
    public void testKafkaSend() {
        logger.info("Testing Kafka send...");
        sendMessage("reddit_posts", "Test message");
    }

    public void sendMessage(String topic, String message) {
        logger.info("Attempting to send message to Kafka topic {}: {}", topic, message);

        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, message);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                logger.info("Message successfully sent to Kafka topic {}: {}", topic, message);
            } else {
                logger.error("Failed to send message to Kafka topic {}: {}", topic, ex);
            }
        });
    }
}


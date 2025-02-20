package com.example.ingestion.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.kafka.support.SendResult;
import java.util.concurrent.CompletableFuture;

@Service
public class KafkaProducerService {
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Sends a message to a specified Kafka topic.
     *
     * @param topic   The Kafka topic to publish to.
     * @param key     The key for partitioning.
     * @param message The message payload.
     */
    public void sendMessage(String topic, String key, String message) {
        logger.info("Attempting to send message to Kafka topic {} with key {}: {}", topic, key, message);

        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, key, message);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                logger.info("Message successfully sent to Kafka topic {}: {}", topic, message);
            } else {
                logger.error("Failed to send message to Kafka topic {}: {}", topic, ex.getMessage());
            }
        });
    }
}

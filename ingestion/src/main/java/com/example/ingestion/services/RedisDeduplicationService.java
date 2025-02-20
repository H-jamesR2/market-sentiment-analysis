package com.example.ingestion.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class RedisDeduplicationService {
    private final HashOperations<String, String, String> hashOperations;

    @Value("${deduplication.expiry.hours:24}")
    private long expiryHours;

    public RedisDeduplicationService(StringRedisTemplate redisTemplate) {
        this.hashOperations = redisTemplate.opsForHash();
    }

    /**
     * Checks if a post/comment has changed since the last stored version.
     * If it has changed, updates Redis and returns true; otherwise, returns false.
     */
    public boolean isDuplicateOrUnchanged(String id, Map<String, String> newData, String type) {
        String redisKey = "dedup:" + type;  // e.g., "dedup:posts"
        String existingHash = hashOperations.get(redisKey, id);

        if (existingHash != null && existingHash.equals(newData.toString())) {
            return true; // Data has not changed
        }

        // Store the new data and set expiry
        hashOperations.put(redisKey, id, newData.toString());
        hashOperations.getOperations().expire(redisKey, expiryHours, TimeUnit.HOURS);
        return false;
    }
}

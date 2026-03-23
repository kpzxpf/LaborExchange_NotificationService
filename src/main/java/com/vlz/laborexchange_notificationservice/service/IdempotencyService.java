package com.vlz.laborexchange_notificationservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Redis-based idempotency guard for Kafka message consumers.
 * Prevents duplicate email sending when Kafka redelivers messages (at-least-once guarantee).
 * TTL = 7 days (covers max redelivery window).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final StringRedisTemplate redisTemplate;

    private static final Duration TTL = Duration.ofDays(7);
    private static final String KEY_PREFIX = "notif:processed:";

    /**
     * Try to mark event as processed.
     * @return true if this is a new event (process it), false if already processed (skip it).
     */
    public boolean tryMarkProcessed(String eventId) {
        String key = KEY_PREFIX + eventId;
        // setIfAbsent = SETNX (atomic)
        Boolean isNew = redisTemplate.opsForValue().setIfAbsent(key, "1", TTL);
        if (Boolean.FALSE.equals(isNew)) {
            log.debug("Duplicate event skipped: {}", eventId);
            return false;
        }
        return true;
    }
}

package com.vlz.laborexchange_notificationservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IdempotencyServiceTest {

    @Mock StringRedisTemplate redisTemplate;
    @Mock ValueOperations<String, String> valueOps;
    @InjectMocks IdempotencyService service;

    @Test
    void tryMarkProcessed_returnsTrue_whenNew() {
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.setIfAbsent(anyString(), eq("1"), any(Duration.class))).thenReturn(true);
        assertThat(service.tryMarkProcessed("event-1")).isTrue();
    }

    @Test
    void tryMarkProcessed_returnsFalse_whenDuplicate() {
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.setIfAbsent(anyString(), eq("1"), any(Duration.class))).thenReturn(false);
        assertThat(service.tryMarkProcessed("event-1")).isFalse();
    }

    @Test
    void tryMarkProcessed_returnsFalse_whenRedisReturnsNull() {
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.setIfAbsent(anyString(), eq("1"), any(Duration.class))).thenReturn(null);
        // null from Redis means setIfAbsent failed — treat as duplicate (safe default)
        assertThat(service.tryMarkProcessed("event-1")).isFalse();
    }
}

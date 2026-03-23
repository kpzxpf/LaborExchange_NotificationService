package com.vlz.laborexchange_notificationservice.controller;

import com.vlz.laborexchange_notificationservice.sse.SseEmitterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class SseNotificationController {

    private final SseEmitterRegistry registry;

    /**
     * SSE endpoint. Gateway injects X-User-Id from JWT (token passed as ?token= query param
     * because EventSource API cannot send Authorization headers).
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@RequestHeader("X-User-Id") Long userId) {
        log.info("SSE connection opened for userId={}", userId);
        return registry.register(userId);
    }
}

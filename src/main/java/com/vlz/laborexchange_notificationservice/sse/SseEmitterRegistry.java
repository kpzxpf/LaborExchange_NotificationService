package com.vlz.laborexchange_notificationservice.sse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Slf4j
@Component
public class SseEmitterRegistry {

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter register(Long userId) {
        SseEmitter emitter = new SseEmitter(0L); // no timeout — client reconnects on disconnect
        emitters.put(userId, emitter);
        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError(e -> emitters.remove(userId));
        log.debug("SSE emitter registered for userId={}", userId);
        return emitter;
    }

    public void push(Long userId, String jsonData) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter == null) return;
        try {
            emitter.send(SseEmitter.event().data(jsonData));
        } catch (IOException e) {
            log.debug("SSE send failed for userId={}, removing emitter", userId);
            emitters.remove(userId);
        }
    }
}

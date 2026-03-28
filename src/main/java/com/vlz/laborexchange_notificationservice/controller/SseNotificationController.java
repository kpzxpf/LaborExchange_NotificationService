package com.vlz.laborexchange_notificationservice.controller;

import com.vlz.laborexchange_notificationservice.dto.StoredNotificationDto;
import com.vlz.laborexchange_notificationservice.entity.Notification;
import com.vlz.laborexchange_notificationservice.repository.NotificationRepository;
import com.vlz.laborexchange_notificationservice.sse.SseEmitterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class SseNotificationController {

    private final SseEmitterRegistry registry;
    private final NotificationRepository notificationRepository;

    /**
     * SSE endpoint. Gateway injects X-User-Id from JWT (token passed as ?token= query param
     * because EventSource API cannot send Authorization headers).
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@RequestHeader("X-User-Id") Long userId) {
        log.info("SSE connection opened for userId={}", userId);
        return registry.register(userId);
    }

    @GetMapping("/my")
    public List<StoredNotificationDto> getMyNotifications(@RequestHeader("X-User-Id") Long userId) {
        LocalDateTime since = LocalDateTime.now().minusDays(10);
        return notificationRepository
                .findByUserIdAndCreatedAtAfterOrderByCreatedAtDesc(userId, since)
                .stream()
                .map(n -> StoredNotificationDto.builder()
                        .id(n.getId())
                        .type(n.getType().name())
                        .message(n.getMessage())
                        .isRead(n.isRead())
                        .createdAt(n.getCreatedAt())
                        .build())
                .toList();
    }

    @GetMapping("/unread-count")
    public long getUnreadCount(@RequestHeader("X-User-Id") Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Transactional
    @PutMapping("/read-all")
    public void markAllRead(@RequestHeader("X-User-Id") Long userId) {
        notificationRepository.markAllReadByUserId(userId);
        log.info("Marked all notifications as read for userId={}", userId);
    }
}

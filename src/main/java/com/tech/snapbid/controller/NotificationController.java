package com.tech.snapbid.controller;

import com.tech.snapbid.dto.NotificationDto;
import com.tech.snapbid.models.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tech.snapbid.service.NotificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public Page<NotificationDto> list(@AuthenticationPrincipal User user,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "20") int size) {
        return notificationService.getUserNotifications(user, PageRequest.of(page, size));
    }

    @GetMapping("/unread-count")
    public Long unreadCount(@AuthenticationPrincipal User user) {
        return notificationService.unreadCount(user);
    }

    @PostMapping("/{id}/read")
    public void markRead(@AuthenticationPrincipal User user, @PathVariable Long id){
        notificationService.markRead(user, id);
    }

}
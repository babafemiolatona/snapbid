package com.tech.snapbid.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tech.snapbid.dto.NotificationDto;
import com.tech.snapbid.models.Notification;
import com.tech.snapbid.models.User;

public interface NotificationService {

    Notification createOutbid(User user,
                              Long auctionId,
                              BigDecimal yourLastBid,
                              BigDecimal newHighestBid,
                              String newHighestBidder);

    Page<NotificationDto> getUserNotifications(User user, Pageable pageable);

    long unreadCount(User user);
    
    void markRead(User user, Long notificationId);

    void markReadBatch(User user, List<Long> ids);

    int markAllRead(User user, LocalDateTime ts);
    
}
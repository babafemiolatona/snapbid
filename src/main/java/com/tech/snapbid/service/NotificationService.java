package com.tech.snapbid.service;

import java.math.BigDecimal;

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
    
}
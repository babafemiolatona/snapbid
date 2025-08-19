package com.tech.snapbid.service;

import com.tech.snapbid.dto.NotificationDto;
import com.tech.snapbid.exceptions.ResourceNotFoundException;
import com.tech.snapbid.models.Notification;
import com.tech.snapbid.models.NotificationType;
import com.tech.snapbid.models.User;
import com.tech.snapbid.realtime.RealtimePublisher;
import com.tech.snapbid.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    private final RealtimePublisher realtimePublisher;

    @Override
    public Notification createOutbid(User user, Long auctionId, BigDecimal yourLastBid,
                                     BigDecimal newHighestBid, String newHighestBidder) {
        Notification n = Notification.builder()
                .user(user)
                .type(NotificationType.OUTBID)
                .auctionId(auctionId)
                .yourLastBid(yourLastBid)
                .newHighestBid(newHighestBid)
                .newHighestBidder(newHighestBidder)
                .build();
        Notification saved = notificationRepository.save(n);
        pushUnreadMetaAfterCommit(user);
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationDto> getUserNotifications(User user, Pageable pageable) {
        return notificationRepository
                .findByUserOrderByCreatedAtDesc(user, pageable)
                .map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public long unreadCount(User user) {
        return notificationRepository.countByUserAndReadAtIsNull(user);
    }

    @Override
    public void markRead(User user, Long notificationId) {
        Notification n = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        if (!n.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Not yours");
        }
        if (n.getReadAt() == null) {
            n.setReadAt(LocalDateTime.now());
            pushUnreadMetaAfterCommit(user);
        }
    }

    @Override
    public void markReadBatch(User user, List<Long> ids) {
        if (ids == null || ids.isEmpty()) return;
        List<Notification> list = notificationRepository.findAllById(ids);
        LocalDateTime now = LocalDateTime.now();
        boolean changed = false;
        for (Notification n : list) {
            if (n.getUser().getId().equals(user.getId()) && n.getReadAt() == null) {
                n.setReadAt(now);
                changed = true;
            }
        }
        if (changed) pushUnreadMetaAfterCommit(user);
    }

    @Override
    public int markAllRead(User user, LocalDateTime ts) {
        int updated = notificationRepository.markAllReadByUserId(user.getId(), ts);
        if (updated > 0) pushUnreadMetaAfterCommit(user);
        return updated;
    }

    private void pushUnreadMetaAfterCommit(User user) {
        long count = unreadCount(user);
        realtimePublisher.publishNotificationMetaAfterCommit(user.getUsername(), count);
    }

    private NotificationDto toDto(Notification n) {
        return NotificationDto.builder()
                .id(n.getId())
                .type(n.getType().name())
                .auctionId(n.getAuctionId())
                .yourLastBid(n.getYourLastBid())
                .newHighestBid(n.getNewHighestBid())
                .newHighestBidder(n.getNewHighestBidder())
                .createdAt(n.getCreatedAt())
                .read(n.isRead())
                .build();
    }
}
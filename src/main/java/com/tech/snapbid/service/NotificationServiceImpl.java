package com.tech.snapbid.service;

import com.tech.snapbid.dto.NotificationDto;
import com.tech.snapbid.models.Notification;
import com.tech.snapbid.models.NotificationType;
import com.tech.snapbid.models.User;
import com.tech.snapbid.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

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
        return notificationRepository.save(n);
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
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        if (!n.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Not yours");
        }
        if (n.getReadAt() == null) {
            n.setReadAt(LocalDateTime.now());
        }
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
package com.tech.snapbid.realtime;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.tech.snapbid.dto.AuctionClosedUpdateDto;
import com.tech.snapbid.dto.AuctionStatusUpdateDto;
import com.tech.snapbid.dto.BidUpdateDto;
import com.tech.snapbid.dto.OutbidNotificationDto;

import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component
@RequiredArgsConstructor
public class RealtimePublisher {

    private final SimpMessagingTemplate template;

    private static final Logger logger = LoggerFactory.getLogger(RealtimePublisher.class);

    public void publishBid(BidUpdateDto dto) {
        logger.info("WS publish BID auction={} bidId={} amount={}", dto.getAuctionId(), dto.getBidId(), dto.getAmount());
        template.convertAndSend("/topic/auction/" + dto.getAuctionId() + "/bid", dto);
    }

    public void publishStatus(AuctionStatusUpdateDto dto) {
        logger.info("WS publish STATUS auction={} status={}", dto.getAuctionId(), dto.getStatus());
        template.convertAndSend("/topic/auction/" + dto.getAuctionId() + "/status", dto);
    }

    public void publishClosed(AuctionClosedUpdateDto dto) {
        logger.info("WS publish CLOSED auction={} winner={} price={}",
                 dto.getAuctionId(), dto.getWinnerUsername(), dto.getFinalPrice());
        template.convertAndSend("/topic/auction/" + dto.getAuctionId() + "/closed", dto);
    }

    public void publishOutbid(String previousBidderUsername, OutbidNotificationDto dto) {
        template.convertAndSendToUser(previousBidderUsername, "/queue/outbid", dto);
    }

    public void publishBidAfterCommit(BidUpdateDto dto) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override public void afterCommit() { publishBid(dto); }
            });
        } else {
            publishBid(dto);
        }
    }

    public void publishOutbidAfterCommit(String previousBidderUsername, OutbidNotificationDto dto) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override public void afterCommit() {
                    publishOutbid(previousBidderUsername, dto);
                }
            });
        } else {
            publishOutbid(previousBidderUsername, dto);
        }
    }

    public void publishNotificationMeta(String username, long unreadCount) {
        template.convertAndSendToUser(username, "/queue/notification-meta",
                Map.of("unreadCount", unreadCount));
    }

    public void publishNotificationMetaAfterCommit(String username, long unreadCount) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override public void afterCommit() {
                    publishNotificationMeta(username, unreadCount);
                }
            });
        } else {
            publishNotificationMeta(username, unreadCount);
        }
    }

    public void publishAuctionStatusAfterCommit(AuctionStatusUpdateDto dto) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override public void afterCommit() { publishAuctionStatus(dto); }
            });
        } else {
            publishAuctionStatus(dto);
        }
    }

    public void publishAuctionStatus(AuctionStatusUpdateDto dto) {
        template.convertAndSend("/topic/auction/" + dto.getAuctionId() + "/status", dto);
    }

}

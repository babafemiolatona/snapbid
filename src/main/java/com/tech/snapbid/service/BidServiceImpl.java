package com.tech.snapbid.service;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.tech.snapbid.dto.AuctionStatusUpdateDto;
import com.tech.snapbid.dto.BidResponseDto;
import com.tech.snapbid.dto.BidUpdateDto;
import com.tech.snapbid.dto.OutbidNotificationDto;
import com.tech.snapbid.exceptions.ResourceNotFoundException;
import com.tech.snapbid.exceptions.AuctionClosedException;
import com.tech.snapbid.exceptions.AuctionCancelledException;
import com.tech.snapbid.exceptions.BidTooLowException;
import com.tech.snapbid.exceptions.AuctionNotStartedException;
import com.tech.snapbid.mapper.BidMapper;
import com.tech.snapbid.models.AuctionItem;
import com.tech.snapbid.models.AuctionStatus;
import com.tech.snapbid.models.Bid;
import com.tech.snapbid.models.Notification;
import com.tech.snapbid.models.User;
import com.tech.snapbid.realtime.RealtimePublisher;
import com.tech.snapbid.repository.AuctionItemRepository;
import com.tech.snapbid.repository.BidRepository;
import com.tech.snapbid.utils.RetryExecutor;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class BidServiceImpl implements BidService {

    @Value("${auction.min-bid-increment:1.0}")
    private BigDecimal minBidIncrement;

    private final AuctionItemRepository auctionItemRepository;
    private final BidRepository bidRepository;
    private final RealtimePublisher realtimePublisher;
    private final NotificationService notificationService;
    private final RetryExecutor retryExecutor;

    @Value("${bid.optimistic.max-retries:5}")
    private int maxRetries;

    @Value("${auction.antiSniping.thresholdSeconds:60}")
    private long antiSnipingThresholdSeconds;

    @Value("${auction.antiSniping.extendSeconds:120}")
    private long antiSnipingExtendSeconds;

    @Value("${auction.antiSniping.maxExtensions:3}")
    private int antiSnipingMaxExtensions;

    @Override
    public BidResponseDto placeBid(Long auctionItemId, BigDecimal amount, User bidder) {
        return retryExecutor.execute(
            maxRetries,
            () -> doPlaceBidAttempt(auctionItemId, amount, bidder)
        );
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected BidResponseDto doPlaceBidAttempt(Long auctionItemId, BigDecimal amount, User bidder) {
        AuctionItem item = auctionItemRepository.findById(auctionItemId)
            .orElseThrow(() -> new ResourceNotFoundException("Auction item not found"));

        LocalDateTime now = LocalDateTime.now();

        switch (item.getStatus()) {
            case CLOSED -> throw new AuctionClosedException("Auction closed");
            case CANCELLED -> throw new AuctionCancelledException("Auction cancelled");
            case SCHEDULED -> {
                if (item.getStartTime().isAfter(now)) {
                    throw new AuctionNotStartedException("Auction not started");
                }
                item.setStatus(AuctionStatus.OPEN);
                auctionItemRepository.save(item);
            }
            default -> {}
        }
        if (item.getStatus() != AuctionStatus.OPEN) {
            throw new AuctionClosedException("Bidding not allowed (status=" + item.getStatus() + ")");
        }
        if (now.isAfter(item.getEndTime())) {
            throw new AuctionClosedException("Auction ended");
        }
        if (item.getSeller().getId().equals(bidder.getId())) {
            throw new AccessDeniedException("Sellers cannot bid on their own items");
        }
        if (amount == null) {
            throw new IllegalArgumentException("Amount is required");
        }

        // Single highest query
        Bid previousHighest = bidRepository.findFirstByAuctionItemOrderByAmountDesc(item);
        BigDecimal baseline = previousHighest != null ? previousHighest.getAmount() : item.getStartingPrice();
        BigDecimal minAllowed = baseline.add(minBidIncrement);
        if (amount.compareTo(minAllowed) < 0) {
            throw new BidTooLowException("Bid must be >= " + minAllowed +
                " (current " + baseline + " + increment " + minBidIncrement + ")");
        }

        item.setLastBidAt(now);

        Bid bid = new Bid();
        bid.setAmount(amount);
        bid.setBidder(bidder);
        bid.setAuctionItem(item);
        bidRepository.saveAndFlush(bid);

        // Anti-sniping extension
        long remainingSeconds = java.time.Duration.between(now, item.getEndTime()).getSeconds();
        boolean extended = false;
        if (remainingSeconds <= antiSnipingThresholdSeconds
                && remainingSeconds > 0
                && item.getExtensionCount() < antiSnipingMaxExtensions) {

            item.setEndTime(item.getEndTime().plusSeconds(antiSnipingExtendSeconds));
            item.setExtensionCount(item.getExtensionCount() + 1);
            extended = true;
        }
        if (extended) {
            auctionItemRepository.saveAndFlush(item);
            long newRemaining = java.time.Duration.between(now, item.getEndTime()).getSeconds();
            realtimePublisher.publishAuctionStatusAfterCommit(
                AuctionStatusUpdateDto.builder()
                    .auctionId(item.getId())
                    .status(item.getStatus().name())
                    .endTime(item.getEndTime())
                    .timeRemainingSeconds(newRemaining)
                    .extensionCount(item.getExtensionCount())
                    .build()
            );
        }

        BidUpdateDto bidDto = BidUpdateDto.builder()
            .auctionId(item.getId())
            .bidId(bid.getId())
            .amount(bid.getAmount())
            .bidderUsername(bidder.getUsername())
            .at(now)
            .build();

        realtimePublisher.publishBidAfterCommit(bidDto);

        // Outbid notification if someone else was highest
        if (previousHighest != null &&
            previousHighest.getBidder() != null &&
            !previousHighest.getBidder().getId().equals(bidder.getId())) {

            Notification savedNotification = notificationService.createOutbid(
                previousHighest.getBidder(),
                item.getId(),
                previousHighest.getAmount(),
                bid.getAmount(),
                bidder.getUsername()
            );

            realtimePublisher.publishOutbidAfterCommit(
                previousHighest.getBidder().getUsername(),
                OutbidNotificationDto.builder()
                    .id(savedNotification.getId())
                    .auctionId(item.getId())
                    .yourLastBid(previousHighest.getAmount())
                    .newHighestBid(bid.getAmount())
                    .newHighestBidder(bidder.getUsername())
                    .at(now)
                    .build()
            );
        }

        return BidMapper.mapToDto(bid);
    }

    @Override
    public Page<BidResponseDto> getBidsForAuctionItem(User bidder, Long auctionItemId, int page, int size) {
        AuctionItem auctionItem = auctionItemRepository.findById(auctionItemId)
            .orElseThrow(() -> new ResourceNotFoundException("Auction item not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "amount"));
        return bidRepository.findByAuctionItem(auctionItem, pageable)
            .map(BidMapper::mapToDto);
    }

    @Override
    public Page<BidResponseDto> getBidsByUser(User bidder, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return bidRepository.findByBidder(bidder, pageable)
            .map(BidMapper::mapToDto);
    }
}

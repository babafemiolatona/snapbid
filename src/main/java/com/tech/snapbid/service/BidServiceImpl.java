package com.tech.snapbid.service;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.tech.snapbid.dto.BidResponseDto;
import com.tech.snapbid.exceptions.ResourceNotFoundException;
import com.tech.snapbid.exceptions.AuctionClosedException;
import com.tech.snapbid.exceptions.AuctionCancelledException;
import com.tech.snapbid.exceptions.BidTooLowException;
import com.tech.snapbid.exceptions.AuctionNotStartedException;
import com.tech.snapbid.mapper.BidMapper;
import com.tech.snapbid.models.AuctionItem;
import com.tech.snapbid.models.AuctionStatus;
import com.tech.snapbid.models.Bid;
import com.tech.snapbid.models.User;
import com.tech.snapbid.repository.AuctionItemRepository;
import com.tech.snapbid.repository.BidRepository;

import jakarta.transaction.Transactional;

@Service
public class BidServiceImpl implements BidService {

    @Value("${auction.min-bid-increment:1.0}")
    private Double minBidIncrement;

    @Autowired
    private AuctionItemRepository auctionItemRepository;

    @Autowired
    private BidRepository bidRepository;

    @Override
    @Transactional
    public BidResponseDto placeBid(Long auctionItemId, BigDecimal amount, User bidder) {
        AuctionItem item = auctionItemRepository.findById(auctionItemId)
            .orElseThrow(() -> new ResourceNotFoundException("Auction item not found"));

        LocalDateTime now = LocalDateTime.now();

        if (item.getStatus() == AuctionStatus.CLOSED) {
            throw new AuctionClosedException("Auction closed");
        }
        if (item.getStatus() == AuctionStatus.CANCELLED) {
            throw new AuctionCancelledException("Auction cancelled");
        }
        if (item.getStatus() == AuctionStatus.SCHEDULED) {
            // Scheduler should promote; enforce rule clearly
            if (item.getStartTime().isAfter(now)) {
                throw new AuctionNotStartedException("Auction not started");
            } else {
                // Start time passed but scheduler hasn't promoted yet; perform a safe inline promotion
                item.setStatus(AuctionStatus.OPEN);
                auctionItemRepository.save(item);
            }
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

        Bid highest = bidRepository.findFirstByAuctionItemOrderByAmountDesc(item);
        double baseline = (highest != null ? highest.getAmount() : item.getStartingPrice());
        double minAllowed = baseline + minBidIncrement;
        double bidValue = amount.doubleValue();

        if (bidValue < minAllowed) {
            throw new BidTooLowException("Bid must be >= " + minAllowed + " (current " + baseline + " + increment " + minBidIncrement + ")");
        }

        Bid bid = new Bid();
        bid.setAmount(bidValue);
        bid.setBidder(bidder);
        bid.setAuctionItem(item);
        bidRepository.save(bid);

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

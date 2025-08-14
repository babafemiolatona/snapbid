package com.tech.snapbid.service;

import java.time.LocalDateTime;
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
import com.tech.snapbid.mapper.BidMapper;
import com.tech.snapbid.models.AuctionItem;
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

    @Transactional
    public BidResponseDto placeBid(Long auctionItemId, Double amount, User bidder) {
        AuctionItem item = auctionItemRepository.findById(auctionItemId)
            .orElseThrow(() -> new ResourceNotFoundException("Auction item not found"));

        if (item.getSeller().getId().equals(bidder.getId())) {
            throw new AccessDeniedException("Sellers cannot bid on their own items");
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(item.getStartTime()) || now.isAfter(item.getEndTime())) {
            throw new IllegalStateException("Auction is not active");
        }

        Bid highest = bidRepository.findFirstByAuctionItemOrderByAmountDesc(item);
        double minAllowed = (highest != null ? highest.getAmount() : item.getStartingPrice()) + minBidIncrement;
            if (amount < minAllowed) {
                throw new IllegalArgumentException("Bid must be at least " + minAllowed);
            }

            Bid bid = new Bid();
            bid.setAmount(amount);
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

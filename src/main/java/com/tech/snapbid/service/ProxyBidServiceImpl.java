package com.tech.snapbid.service;

import com.tech.snapbid.dto.ApiResponse;
import com.tech.snapbid.dto.ProxyBidRequest;
import com.tech.snapbid.dto.ProxyBidResponseDto;
import com.tech.snapbid.exceptions.ResourceNotFoundException;
import com.tech.snapbid.mapper.ProxyBidMapper;
import com.tech.snapbid.models.AuctionItem;
import com.tech.snapbid.models.Bid;
import com.tech.snapbid.models.ProxyBid;
import com.tech.snapbid.models.User;
import com.tech.snapbid.repository.AuctionItemRepository;
import com.tech.snapbid.repository.BidRepository;
import com.tech.snapbid.repository.ProxyBidRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProxyBidServiceImpl implements ProxyBidService {

    @Value("${bid.minimum.increment:10}")
    private BigDecimal defaultMinIncrement;

    private final ProxyBidRepository proxyBidRepository;
    private final AuctionItemRepository auctionItemRepository;
    private final BidService bidService;    
    private final BidRepository bidRepository;

    public ProxyBidServiceImpl(
        ProxyBidRepository proxyBidRepository,
        AuctionItemRepository auctionItemRepository,
        BidRepository bidRepository,
        @Lazy BidService bidService
    ) {
        this.proxyBidRepository = proxyBidRepository;
        this.auctionItemRepository = auctionItemRepository;
        this.bidRepository = bidRepository;
        this.bidService = bidService;
    }

    private BigDecimal resolveCurrentPrice(AuctionItem auction) {
        Optional<Bid> top = bidRepository.findTopByAuctionItemIdOrderByAmountDesc(auction.getId());
        return top.map(Bid::getAmount)
                  .orElseGet(() -> auction.getStartingPrice() != null ? auction.getStartingPrice() : BigDecimal.ZERO);
    }

    @Override
    @Transactional
    public ProxyBidResponseDto createProxyBid(Long auctionId, ProxyBidRequest request, User bidder) {
        AuctionItem auction = auctionItemRepository.findById(auctionId)
            .orElseThrow(() -> new ResourceNotFoundException("Auction not found: " + auctionId));

        // deactivate any existing active proxy for this user+auction
        proxyBidRepository.findByAuctionItemAndBidderAndActiveTrue(auction, bidder)
            .ifPresent(existing -> {
                existing.setActive(false);
                proxyBidRepository.save(existing);
            });

        ProxyBid proxy = new ProxyBid();
        proxy.setAuctionItem(auction);
        proxy.setBidder(bidder);
        proxy.setMaxAmount(request.getMaxAmount());
        proxy.setActive(true);
        ProxyBid saved = proxyBidRepository.save(proxy);

        // optionally place an initial automated bid if current price + minInc <= proxy max
        BigDecimal current = resolveCurrentPrice(auction);
        BigDecimal minInc = defaultMinIncrement != null ? defaultMinIncrement : BigDecimal.ONE;
        BigDecimal next = current.add(minInc);
        if (next.compareTo(request.getMaxAmount()) <= 0) {
            bidService.placeBid(auctionId, next, bidder);
        }

        return ProxyBidMapper.toDto(saved);
    }

    @Override
    @Transactional
    public ApiResponse cancelProxyBid(Long auctionId, User bidder) {
        AuctionItem auction = auctionItemRepository.findById(auctionId)
            .orElseThrow(() -> new ResourceNotFoundException("Auction not found: " + auctionId));

        ProxyBid saved = proxyBidRepository.findByAuctionItemAndBidderAndActiveTrue(auction, bidder)
            .map(p -> {
                p.setActive(false);
                return proxyBidRepository.save(p);
            })
            .orElseThrow(() -> new ResourceNotFoundException("No active proxy bid found for auction: " + auctionId));

        ProxyBidMapper.toDto(saved);
        return new ApiResponse(true, "Proxy bid cancelled successfully");


    }

    @Override
    @Transactional
    public void processNewBid(Long auctionId, BigDecimal newBidAmount, User bidder) {
        AuctionItem auction = auctionItemRepository.findById(auctionId)
            .orElseThrow(() -> new ResourceNotFoundException("Auction not found: " + auctionId));

        BigDecimal minInc = defaultMinIncrement != null ? defaultMinIncrement : BigDecimal.ONE;
        BigDecimal current = newBidAmount != null ? newBidAmount : resolveCurrentPrice(auction);
        if (current == null) current = BigDecimal.ZERO;

        List<ProxyBid> activeProxies = proxyBidRepository.findByAuctionItemAndActiveTrueOrderByMaxAmountDesc(auction);
        for (ProxyBid proxy : activeProxies) {
            if (proxy.getBidder().equals(bidder)) continue;

            if (proxy.getMaxAmount().compareTo(current) > 0) {
                BigDecimal candidate = current.add(minInc);
                if (candidate.compareTo(proxy.getMaxAmount()) <= 0) {
                    bidService.placeBid(auctionId, candidate, proxy.getBidder());
                    return; // place single automatic counter per incoming bid
                } else {
                    // place up to proxy max if candidate would exceed it
                    bidService.placeBid(auctionId, proxy.getMaxAmount(), proxy.getBidder());
                    return;
                }
            }
        }
    }
}
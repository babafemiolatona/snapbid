package com.tech.snapbid.service;

import com.tech.snapbid.dto.ApiResponse;
import com.tech.snapbid.dto.ProxyBidRequest;
import com.tech.snapbid.dto.ProxyBidResponseDto;
import com.tech.snapbid.models.User;
import java.math.BigDecimal;

public interface ProxyBidService {
    ProxyBidResponseDto createProxyBid(Long auctionId, ProxyBidRequest request, User bidder);
    ApiResponse cancelProxyBid(Long auctionId, User bidder);
    void processNewBid(Long auctionId, BigDecimal newBidAmount, User bidder);
}
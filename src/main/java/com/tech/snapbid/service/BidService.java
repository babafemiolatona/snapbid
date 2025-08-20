package com.tech.snapbid.service;

import org.springframework.data.domain.Page;

import com.tech.snapbid.dto.BidResponseDto;

import java.math.BigDecimal;
import com.tech.snapbid.models.User;

public interface BidService {

    BidResponseDto placeBid(Long auctionItemId, BigDecimal amount, User bidder);
    Page<BidResponseDto> getBidsForAuctionItem(User bidder, Long auctionItemId, int page, int size);
    Page<BidResponseDto> getBidsByUser(User bidder, int page, int size);

}

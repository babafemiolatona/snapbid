package com.tech.snapbid.repository;

import com.tech.snapbid.models.AuctionItem;
import com.tech.snapbid.models.ProxyBid;
import com.tech.snapbid.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface ProxyBidRepository extends JpaRepository<ProxyBid, Long> {
    Optional<ProxyBid> findByAuctionItemAndBidderAndActiveTrue(
        AuctionItem auctionItem, 
        User bidder
    );
    
    List<ProxyBid> findByAuctionItemAndActiveTrueOrderByMaxAmountDesc(
        AuctionItem auctionItem
    );
}
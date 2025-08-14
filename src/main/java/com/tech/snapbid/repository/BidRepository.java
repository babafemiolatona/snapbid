package com.tech.snapbid.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tech.snapbid.models.AuctionItem;
import com.tech.snapbid.models.Bid;
import com.tech.snapbid.models.User;

public interface BidRepository extends JpaRepository<Bid, Long> {

    // All bids for an item (sorting decided by Pageable)
    Page<Bid> findByAuctionItem(AuctionItem auctionItem, Pageable pageable);

    // Convenience: list all (legacy / non-paged usage if needed)
    List<Bid> findByAuctionItemOrderByAmountDesc(AuctionItem auctionItem);

    // Bids placed by a user, newest first
    Page<Bid> findByBidderOrderByCreatedAtDesc(User bidder, Pageable pageable);

    // Paged bids by a user (sorting provided via Pageable)
    Page<Bid> findByBidder(User bidder, Pageable pageable);

    // Highest bid quick lookup
    Bid findFirstByAuctionItemOrderByAmountDesc(AuctionItem auctionItem);
}

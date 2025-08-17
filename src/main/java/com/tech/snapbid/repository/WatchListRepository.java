package com.tech.snapbid.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tech.snapbid.models.AuctionItem;
import com.tech.snapbid.models.User;
import com.tech.snapbid.models.WatchList;

public interface WatchListRepository extends JpaRepository<WatchList, Long> {

    Optional<WatchList> findByUserAndAuctionItem(User user, AuctionItem item);

    Page<WatchList> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    boolean existsByUserAndAuctionItem(User user, AuctionItem item);
    
    void deleteByUserAndAuctionItem(User user, AuctionItem item);
}

package com.tech.snapbid.repository;

import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tech.snapbid.models.AuctionItem;
import com.tech.snapbid.models.AuctionStatus;
import com.tech.snapbid.models.User;

public interface AuctionItemRepository extends JpaRepository<AuctionItem, Long> {

    Page<AuctionItem> findBySeller(User seller, Pageable pageable);

    List<AuctionItem> findTop100ByStatusAndEndTimeBeforeOrderByEndTimeAsc(AuctionStatus status, LocalDateTime cutoff);

    Page<AuctionItem> findByStatusAndEndTimeBefore(AuctionStatus status,LocalDateTime cutoff,Pageable pageable);
}

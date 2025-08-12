package com.tech.snapbid.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tech.snapbid.models.AuctionItem;

public interface AuctionItemRepository extends JpaRepository<AuctionItem, Long> {

    List<AuctionItem> findBySellerId(Long sellerId);

}

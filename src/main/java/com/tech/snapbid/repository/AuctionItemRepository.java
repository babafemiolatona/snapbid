package com.tech.snapbid.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tech.snapbid.models.AuctionItem;
import com.tech.snapbid.models.User;

public interface AuctionItemRepository extends JpaRepository<AuctionItem, Long> {

    Page<AuctionItem> findBySeller(User seller, Pageable pageable);

}

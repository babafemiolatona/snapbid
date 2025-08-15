package com.tech.snapbid.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tech.snapbid.models.AuctionItem;
import com.tech.snapbid.models.AuctionStatus;
import com.tech.snapbid.models.Bid;
import com.tech.snapbid.repository.AuctionItemRepository;
import com.tech.snapbid.repository.BidRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuctionClosingService {

    @Autowired
    private AuctionItemRepository auctionItemRepository;
   
    @Autowired
    private BidRepository bidRepository;

    // Batch driver
    @Transactional
    public int closeExpiredAuctionsBatch(int batchSize) {
        LocalDateTime now = LocalDateTime.now();
        List<AuctionItem> items = auctionItemRepository
            .findTop100ByStatusAndEndTimeBeforeOrderByEndTimeAsc(AuctionStatus.OPEN, now);

        int processed = 0;
        for (AuctionItem item : items.stream().limit(batchSize).toList()) {
            try {
                closeSingle(item);
                processed++;
            } catch (OptimisticLockingFailureException e) {
                log.debug("Optimistic conflict closing auction {}", item.getId());
            } catch (Exception e) {
                log.error("Failed closing auction {}: {}", item.getId(), e.getMessage());
            }
        }
        return processed;
    }

    // Isolated transactional close for one item
    @Transactional
    public void closeAuctionById(Long id) {
        AuctionItem item = auctionItemRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Auction not found"));
        closeSingle(item);
    }

    private void closeSingle(AuctionItem item) {
        if (item.getStatus() != AuctionStatus.OPEN) return;
        if (LocalDateTime.now().isBefore(item.getEndTime())) return; // not yet ended

        Bid highest = bidRepository.findFirstByAuctionItemOrderByAmountDesc(item);
        if (highest != null) {
            item.setWinner(highest.getBidder());
            item.setFinalPrice(highest.getAmount());
        } else {
            item.setWinner(null);
            item.setFinalPrice(null);
        }
        item.setStatus(AuctionStatus.CLOSED);
        auctionItemRepository.save(item);
    }
}
package com.tech.snapbid.jobs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.tech.snapbid.service.AuctionClosingService;
import com.tech.snapbid.repository.AuctionItemRepository;
import com.tech.snapbid.models.AuctionItem;
import com.tech.snapbid.models.AuctionStatus;
import java.time.LocalDateTime;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuctionClosingJob {

    @Autowired
    private AuctionClosingService closingService;

    @Autowired
    private AuctionItemRepository auctionItemRepository;

    @Value("${auction.close.batch-size:50}")
    private int batchSize;

    // Every 15s default
    @Scheduled(fixedDelayString = "${auction.close.scan-interval-ms:15000}")
    public void scanAndClose() {
        LocalDateTime now = LocalDateTime.now();

        // Promote due SCHEDULED auctions to OPEN
        List<AuctionItem> toOpen = auctionItemRepository
            .findTop100ByStatusAndStartTimeBeforeOrderByStartTimeAsc(AuctionStatus.SCHEDULED, now);
        int promoted = 0;
        for (AuctionItem item : toOpen) {
            if (item.getStartTime().isBefore(now) || item.getStartTime().isEqual(now)) {
                item.setStatus(AuctionStatus.OPEN);
                auctionItemRepository.save(item);
                promoted++;
            }
        }

        int closed = closingService.closeExpiredAuctionsBatch(batchSize);
        if (promoted > 0 || closed > 0) {
            log.info("Lifecycle tick: promoted {} -> OPEN, closed {}", promoted, closed);
        }
    }
}
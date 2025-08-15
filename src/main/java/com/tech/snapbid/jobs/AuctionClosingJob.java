package com.tech.snapbid.jobs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.tech.snapbid.service.AuctionClosingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuctionClosingJob {

    @Autowired
    private AuctionClosingService closingService;

    @Value("${auction.close.batch-size:50}")
    private int batchSize;

    // Every 15s default
    @Scheduled(fixedDelayString = "${auction.close.scan-interval-ms:15000}")
    public void scanAndClose() {
        int closed = closingService.closeExpiredAuctionsBatch(batchSize);
        if (closed > 0) {
            log.info("Closed {} auctions", closed);
        }
    }
}
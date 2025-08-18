package com.tech.snapbid.realtime;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.tech.snapbid.dto.AuctionClosedUpdateDto;
import com.tech.snapbid.dto.AuctionStatusUpdateDto;
import com.tech.snapbid.dto.BidUpdateDto;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component
@RequiredArgsConstructor
public class RealtimePublisher {

    private final SimpMessagingTemplate template;

    private static final Logger logger = LoggerFactory.getLogger(RealtimePublisher.class);

    public void publishBid(BidUpdateDto dto) {
        logger.info("WS publish BID auction={} bidId={} amount={}", dto.getAuctionId(), dto.getBidId(), dto.getAmount());
        template.convertAndSend("/topic/auction/" + dto.getAuctionId() + "/bid", dto);
    }

    public void publishStatus(AuctionStatusUpdateDto dto) {
        logger.info("WS publish STATUS auction={} status={}", dto.getAuctionId(), dto.getStatus());
        template.convertAndSend("/topic/auction/" + dto.getAuctionId() + "/status", dto);
    }

    public void publishClosed(AuctionClosedUpdateDto dto) {
        logger.info("WS publish CLOSED auction={} winner={} price={}",
                 dto.getAuctionId(), dto.getWinnerUsername(), dto.getFinalPrice());
        template.convertAndSend("/topic/auction/" + dto.getAuctionId() + "/closed", dto);
    }

}

package com.tech.snapbid.realtime;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.tech.snapbid.dto.AuctionClosedUpdateDto;
import com.tech.snapbid.dto.AuctionStatusUpdateDto;
import com.tech.snapbid.dto.BidUpdateDto;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RealtimePublisher {

    private final SimpMessagingTemplate template;

    public void publishBid(BidUpdateDto dto) {
        template.convertAndSend("/topic/auction/" + dto.getAuctionId() + "/bid", dto);
    }

    public void publishStatus(AuctionStatusUpdateDto dto) {
        template.convertAndSend("/topic/auction/" + dto.getAuctionId() + "/status", dto);
    }

    public void publishClosed(AuctionClosedUpdateDto dto) {
        template.convertAndSend("/topic/auction/" + dto.getAuctionId() + "/closed", dto);
    }

}

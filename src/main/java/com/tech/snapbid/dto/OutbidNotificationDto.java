package com.tech.snapbid.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class OutbidNotificationDto {

    Long id;
    Long auctionId;
    BigDecimal yourLastBid;
    BigDecimal newHighestBid;
    String newHighestBidder;
    LocalDateTime at;

}

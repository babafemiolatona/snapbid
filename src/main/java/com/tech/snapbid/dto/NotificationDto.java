package com.tech.snapbid.dto;

import lombok.Builder;
import lombok.Value;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
@Builder
public class NotificationDto {

    Long id;
    String type;
    Long auctionId;
    BigDecimal yourLastBid;
    BigDecimal newHighestBid;
    String newHighestBidder;
    LocalDateTime createdAt;
    boolean read;

}
package com.tech.snapbid.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AuctionClosedUpdateDto {

    Long auctionId;
    String winnerUsername;
    BigDecimal finalPrice;
    LocalDateTime at;

}

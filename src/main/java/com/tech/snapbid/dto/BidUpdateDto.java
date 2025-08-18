package com.tech.snapbid.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class BidUpdateDto {

    Long auctionId;
    Long bidId;
    BigDecimal amount;
    String bidderUsername;
    LocalDateTime at;

}

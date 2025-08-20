package com.tech.snapbid.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AuctionStatusUpdateDto {

    Long auctionId;
    String status;
    LocalDateTime endTime;
    long timeRemainingSeconds;
    LocalDateTime at;
    int extensionCount;
}

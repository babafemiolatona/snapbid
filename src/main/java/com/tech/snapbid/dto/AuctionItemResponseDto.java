package com.tech.snapbid.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AuctionItemResponseDto {

    private Long id;
    private String title;
    private String description;
    private BigDecimal startingPrice;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String sellerUsername;
    private String status; 
    private String winnerUsername;
    private BigDecimal finalPrice;
    private Long timeRemainingSeconds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}

package com.tech.snapbid.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class WatchlistItemDto {

    private Long auctionItemId;
    private String title;
    private String status;
    private Double currentPrice;
    private LocalDateTime endTime;
    private LocalDateTime addedAt;

}

package com.tech.snapbid.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AuctionItemResponseDto {

    private Long id;
    private String title;
    private String description;
    private String startingPrice;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String sellerUsername;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}

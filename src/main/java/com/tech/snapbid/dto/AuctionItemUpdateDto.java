package com.tech.snapbid.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AuctionItemUpdateDto {

    private String title;
    private String description;
    private Double startingPrice;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

}

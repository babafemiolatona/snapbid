package com.tech.snapbid.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AuctionItemUpdateDto {

    private String title;
    private String description;
    private BigDecimal startingPrice;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

}

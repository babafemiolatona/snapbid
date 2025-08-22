package com.tech.snapbid.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProxyBidResponseDto {
    private Long id;
    private Long auctionId;
    private BigDecimal maxAmount;
    private boolean active;
    private LocalDateTime createdAt;
}
package com.tech.snapbid.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class BidResponseDto {

    private Long id;
    private BigDecimal amount;
    private String bidderUsername;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}

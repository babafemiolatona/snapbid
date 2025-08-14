package com.tech.snapbid.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class BidResponseDto {

    private Long id;
    private Double amount;
    private String bidderUsername;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}

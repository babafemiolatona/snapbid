package com.tech.snapbid.service;

import com.tech.snapbid.dto.AuctionItemResponseDto;
import com.tech.snapbid.models.AuctionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface AuctionSearchService {
    Page<AuctionItemResponseDto> search(String q,
                                        AuctionStatus status,
                                        BigDecimal minPrice,
                                        BigDecimal maxPrice,
                                        LocalDateTime endingBefore,
                                        LocalDateTime endingAfter,
                                        Pageable pageable);
}
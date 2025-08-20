package com.tech.snapbid.controller;

import com.tech.snapbid.dto.AuctionItemResponseDto;
import com.tech.snapbid.models.AuctionStatus;
import com.tech.snapbid.service.AuctionSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/auctions")
@RequiredArgsConstructor
public class PublicAuctionSearchController {

    private final AuctionSearchService searchService;

    @GetMapping("/search")
    public Page<AuctionItemResponseDto> search(
        @RequestParam(required = false) String q,
        @RequestParam(required = false) AuctionStatus status,
        @RequestParam(required = false) BigDecimal minPrice,
        @RequestParam(required = false) BigDecimal maxPrice,
        @RequestParam(required = false) LocalDateTime endingBefore,
        @RequestParam(required = false) LocalDateTime endingAfter,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "endTime,asc") String sort
    ) {
        Pageable pageable = PageRequest.of(page, size, parseSort(sort));
        return searchService.search(q, status, minPrice, maxPrice, endingBefore, endingAfter, pageable);
    }

    private Sort parseSort(String sortParam) {
        try {
            String[] parts = sortParam.split(",", 2);
            String field = parts[0];
            Sort.Direction dir = parts.length > 1 ? Sort.Direction.fromString(parts[1]) : Sort.Direction.ASC;
            return Sort.by(dir, field);
        } catch (Exception e) {
            return Sort.by(Sort.Direction.ASC, "endTime");
        }
    }
}
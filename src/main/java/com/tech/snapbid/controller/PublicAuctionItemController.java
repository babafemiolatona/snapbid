package com.tech.snapbid.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tech.snapbid.dto.AuctionItemResponseDto;
import com.tech.snapbid.service.AuctionItemService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auction-items")
@RequiredArgsConstructor
public class PublicAuctionItemController {

    private final AuctionItemService auctionItemService;
    
    @GetMapping
    public ResponseEntity<Page<AuctionItemResponseDto>> getAllAuctionItems(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Page<AuctionItemResponseDto> items = auctionItemService.getAllAuctionItems(page, size);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuctionItemResponseDto> getAuctionItemById(@PathVariable Long id) {
        AuctionItemResponseDto item = auctionItemService.getPublicAuctionItemById(id);
        return ResponseEntity.ok(item);
    }

    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<Page<AuctionItemResponseDto>> getAuctionItemsBySeller(
        @PathVariable Long sellerId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
    Page<AuctionItemResponseDto> items = auctionItemService.getAllAuctionItemsBySellerId(sellerId, page, size);
        return ResponseEntity.ok(items);
    }
}

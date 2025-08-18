package com.tech.snapbid.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tech.snapbid.dto.ApiResponse;
import com.tech.snapbid.dto.AuctionItemRequestDto;
import com.tech.snapbid.dto.AuctionItemResponseDto;
import com.tech.snapbid.dto.AuctionItemUpdateDto;
import com.tech.snapbid.models.User;
import com.tech.snapbid.service.AuctionItemService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@PreAuthorize("hasRole('SELLER')")
@RequestMapping("api/v1/seller/auction-items")
@RequiredArgsConstructor
public class AuctionItemController {

    private final AuctionItemService auctionItemService;

    @PostMapping
    public ResponseEntity<AuctionItemResponseDto> createAuctionItem(
        @AuthenticationPrincipal User seller,
        @Valid @RequestBody AuctionItemRequestDto auctionItemRequestDto
    ) {
        AuctionItemResponseDto responseDto = auctionItemService.createAuctionItem(seller, auctionItemRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping
    public ResponseEntity<Page<AuctionItemResponseDto>> getSellerAuctionItems(
        @AuthenticationPrincipal User seller,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Page<AuctionItemResponseDto> items = auctionItemService.getAllAuctionItemsBySeller(seller, page, size);
        return ResponseEntity.ok(items);
    }

    @GetMapping("{id}")
    public ResponseEntity<AuctionItemResponseDto> getAuctionItemById(
        @AuthenticationPrincipal User seller,
        @PathVariable Long id
    ) {
        AuctionItemResponseDto item = auctionItemService.getAuctionItemById(seller, id);
        return ResponseEntity.ok(item);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateAuctionItem(
        @AuthenticationPrincipal User seller,
        @PathVariable Long id,
        @Valid @RequestBody AuctionItemUpdateDto dto
    ) {
        AuctionItemResponseDto updatedItem = auctionItemService.updateAuctionItem(seller, id, dto);
        ApiResponse response = new ApiResponse(true, "Auction item updated successfully", updatedItem);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteAuctionItem(
        @AuthenticationPrincipal User seller,
        @PathVariable Long id
    ) {
        ApiResponse response = auctionItemService.deleteAuctionItem(seller, id);
        return ResponseEntity.ok(response);
    }
}

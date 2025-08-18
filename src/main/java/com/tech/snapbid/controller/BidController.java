package com.tech.snapbid.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tech.snapbid.dto.BidResponseDto;
import com.tech.snapbid.dto.PlaceBidRequest;
import com.tech.snapbid.models.User;
import com.tech.snapbid.service.BidService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@PreAuthorize("hasRole('BIDDER')")
@RequestMapping("/api/v1/bids")
@RequiredArgsConstructor
public class BidController {

    private final BidService bidService;

    @PostMapping("/auction/{auctionItemId}")
    public ResponseEntity<BidResponseDto> placeBid(
        @AuthenticationPrincipal User bidder,
        @Valid @RequestBody PlaceBidRequest placeBidRequest,
        @PathVariable Long auctionItemId
    ) {
        BidResponseDto responseDto = bidService.placeBid(auctionItemId, placeBidRequest.getAmount(), bidder);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/auction/{auctionItemId}")
    public ResponseEntity<Page<BidResponseDto>> getBidsForAuctionItem(
        @AuthenticationPrincipal User bidder,
        @PathVariable Long auctionItemId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Page<BidResponseDto> bids = bidService.getBidsForAuctionItem(bidder, auctionItemId, page, size);
        return ResponseEntity.ok(bids);
    }

    @GetMapping("/me")
    public ResponseEntity<Page<BidResponseDto>> getBidsByUser(
        @AuthenticationPrincipal User bidder,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Page<BidResponseDto> bids = bidService.getBidsByUser(bidder, page, size);
        return ResponseEntity.ok(bids);
    }
}

package com.tech.snapbid.controller;

import com.tech.snapbid.dto.ApiResponse;
import com.tech.snapbid.dto.ProxyBidRequest;
import com.tech.snapbid.dto.ProxyBidResponseDto;
import com.tech.snapbid.service.ProxyBidService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.tech.snapbid.models.User;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/proxy-bids")
@PreAuthorize("hasRole('BIDDER')")
@RequiredArgsConstructor
public class ProxyBidController {

    private final ProxyBidService proxyBidService;

    @PostMapping("/auction/{auctionId}")
    public ResponseEntity<ProxyBidResponseDto> createProxyBid(
            @PathVariable Long auctionId,
            @Valid @RequestBody ProxyBidRequest request,
            @AuthenticationPrincipal User bidder) {
        ProxyBidResponseDto response = proxyBidService.createProxyBid(auctionId, request, bidder);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/auction/{auctionId}")
    public ResponseEntity<ApiResponse> cancelProxyBid(
            @PathVariable Long auctionId,
            @AuthenticationPrincipal User bidder) {
        ApiResponse resp = proxyBidService.cancelProxyBid(auctionId, bidder);
        return ResponseEntity.ok(resp);
    }
}
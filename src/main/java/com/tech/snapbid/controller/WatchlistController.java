package com.tech.snapbid.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tech.snapbid.dto.ApiResponse;
import com.tech.snapbid.dto.WatchlistItemDto;
import com.tech.snapbid.models.User;
import com.tech.snapbid.service.WatchlistService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/watchlist")
@RequiredArgsConstructor
public class WatchlistController {

    private final WatchlistService watchlistService;

    @PostMapping("/{auctionItemId}")
    public ResponseEntity<ApiResponse> add(@PathVariable Long auctionItemId, @AuthenticationPrincipal User user) {
        ApiResponse response = watchlistService.add(auctionItemId, user);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{auctionItemId}")
    public ResponseEntity<ApiResponse> remove(@PathVariable Long auctionItemId, @AuthenticationPrincipal User user) {
        ApiResponse response = watchlistService.remove(auctionItemId, user);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<WatchlistItemDto>> list(
        @AuthenticationPrincipal User user,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        Page<WatchlistItemDto> watchlistItems = watchlistService.list(user, page, size);
        return ResponseEntity.ok(watchlistItems);
    }

    @GetMapping("/is-watching/{auctionItemId}")
    public ResponseEntity<Boolean> isWatching(@PathVariable Long auctionItemId, @AuthenticationPrincipal User user) {
        boolean isWatching = watchlistService.isWatching(auctionItemId, user);
        return ResponseEntity.ok(isWatching);
    }
}

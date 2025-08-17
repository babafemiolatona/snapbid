package com.tech.snapbid.service;

import org.springframework.data.domain.Page;

import com.tech.snapbid.dto.ApiResponse;
import com.tech.snapbid.dto.WatchlistItemDto;
import com.tech.snapbid.models.User;

public interface WatchlistService {
    ApiResponse add(Long auctionItemId, User user);
    ApiResponse remove(Long auctionItemId, User user);
    Page<WatchlistItemDto> list(User user, int page, int size);
    boolean isWatching(Long auctionItemId, User user);
}

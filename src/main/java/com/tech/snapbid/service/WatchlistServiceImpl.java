package com.tech.snapbid.service;

import com.tech.snapbid.dto.ApiResponse;
import com.tech.snapbid.dto.WatchlistItemDto;
import com.tech.snapbid.exceptions.ResourceNotFoundException;
import com.tech.snapbid.models.AuctionItem;
import com.tech.snapbid.models.User;
import com.tech.snapbid.models.WatchList;
import com.tech.snapbid.repository.AuctionItemRepository;
import com.tech.snapbid.repository.WatchListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WatchlistServiceImpl implements WatchlistService {

    private static final int MAX_ENTRIES_PER_USER = 500;

    // Constructor-injected (no field @Autowired needed)
    private final WatchListRepository watchlistRepository;
    private final AuctionItemRepository auctionItemRepository;

    @Override
    @Transactional
    public ApiResponse add(Long auctionItemId, User user) {
        AuctionItem item = auctionItemRepository.findById(auctionItemId)
            .orElseThrow(() -> new ResourceNotFoundException("Auction item not found"));

        // Optional: prevent watching own auction
        if (item.getSeller() != null && item.getSeller().getId().equals(user.getId())) {
            return new ApiResponse(false, "Cannot watch your own auction");
        }

        if (watchlistRepository.existsByUserAndAuctionItem(user, item)) {
            return new ApiResponse(true, "Already on watchlist");
        }

        // Per-user count (avoid global count()); reuse existing pageable query to get total
        long userCount = watchlistRepository
            .findByUserOrderByCreatedAtDesc(user, PageRequest.of(0, 1))
            .getTotalElements();

        if (userCount >= MAX_ENTRIES_PER_USER) {
            return new ApiResponse(false, "Watchlist limit reached");
        }

        WatchList entry = WatchList.builder()
            .user(user)
            .auctionItem(item)
            .build();
        watchlistRepository.save(entry);

        return new ApiResponse(true, "Added to watchlist");
    }

    @Override
    @Transactional
    public ApiResponse remove(Long auctionItemId, User user) {
        AuctionItem item = auctionItemRepository.findById(auctionItemId)
            .orElseThrow(() -> new ResourceNotFoundException("Auction item not found"));

        WatchList existing = watchlistRepository.findByUserAndAuctionItem(user, item).orElse(null);
        if (existing == null) {
            return new ApiResponse(false, "Not on watchlist");
        }
        watchlistRepository.delete(existing);
        return new ApiResponse(true, "Removed from watchlist");
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WatchlistItemDto> list(User user, int page, int size) {
        Page<WatchList> entries = watchlistRepository
            .findByUserOrderByCreatedAtDesc(user, PageRequest.of(page, size));
        return entries.map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isWatching(Long auctionItemId, User user) {
        AuctionItem stub = AuctionItem.builder().id(auctionItemId).build();
        return watchlistRepository.existsByUserAndAuctionItem(user, stub);
    }

    private WatchlistItemDto toDto(WatchList e) {
        AuctionItem ai = e.getAuctionItem();
        WatchlistItemDto dto = new WatchlistItemDto();
        dto.setAuctionItemId(ai.getId());
        dto.setTitle(ai.getTitle());
        dto.setStatus(ai.getStatus().name());
        dto.setEndTime(ai.getEndTime());
        // dto.setCurrentPrice(ai.getCurrentPrice()); // if you add that field later
        dto.setAddedAt(e.getCreatedAt());
        return dto;
    }
}
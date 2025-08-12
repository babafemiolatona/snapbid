package com.tech.snapbid.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.tech.snapbid.dto.ApiResponse;
import com.tech.snapbid.dto.AuctionItemRequestDto;
import com.tech.snapbid.dto.AuctionItemUpdateDto;
import com.tech.snapbid.dto.AuctionItemResponseDto;
import com.tech.snapbid.exceptions.ResourceNotFoundException;
import com.tech.snapbid.mapper.AuctionItemMapper;
import com.tech.snapbid.models.AuctionItem;
import com.tech.snapbid.models.User;
import com.tech.snapbid.repository.AuctionItemRepository;

@Service
public class AuctionItemServiceImpl implements AuctionItemService {

    @Autowired
    private AuctionItemRepository auctionItemRepository;

    @Override
    public AuctionItemResponseDto createAuctionItem(User seller, AuctionItemRequestDto dto) {
        AuctionItem item = AuctionItemMapper.fromDto(dto, seller);
        auctionItemRepository.save(item);
        AuctionItemResponseDto responseDto = AuctionItemMapper.toDto(item);
        return responseDto;
    }

    @Override
    public AuctionItemResponseDto updateAuctionItem(User seller, Long id, AuctionItemUpdateDto dto) {
        AuctionItem existingItem = auctionItemRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("AuctionItem not found with id: " + id));

        if (!existingItem.getSeller().equals(seller)) {
            throw new AccessDeniedException("You are not allowed to update this auction item");
        }

        existingItem.setTitle(dto.getTitle() != null ? dto.getTitle() : existingItem.getTitle());
        existingItem.setDescription(dto.getDescription() != null ? dto.getDescription() : existingItem.getDescription());
        existingItem.setStartingPrice(dto.getStartingPrice() != null ? dto.getStartingPrice() : existingItem.getStartingPrice());
        existingItem.setEndTime(dto.getEndTime() != null ? dto.getEndTime() : existingItem.getEndTime());

        auctionItemRepository.save(existingItem);
        return AuctionItemMapper.toDto(existingItem);
    }

    @Override
    public ApiResponse deleteAuctionItem(User seller, Long id) {
        AuctionItem existingItem = auctionItemRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Auction item not found with id: " + id));

        if (!existingItem.getSeller().equals(seller)) {
            throw new AccessDeniedException("You are not allowed to delete this auction item");
        }

        auctionItemRepository.delete(existingItem);
        return new ApiResponse(true, "Auction item deleted successfully");
    }

    @Override
    public Page<AuctionItemResponseDto> getAllAuctionItemsBySeller(User seller, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AuctionItem> auctionItems = auctionItemRepository.findBySeller(seller, pageable);
        return auctionItems.map(AuctionItemMapper::toDto);
    }

    @Override
    public AuctionItemResponseDto getAuctionItemById(User seller, Long id) {
        AuctionItem item = auctionItemRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Auction item not found with id: " + id));

        if (!item.getSeller().equals(seller)) {
            throw new AccessDeniedException("You are not allowed to access this auction item");
        }

        return AuctionItemMapper.toDto(item);
    }
}

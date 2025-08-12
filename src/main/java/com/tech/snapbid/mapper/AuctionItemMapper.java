package com.tech.snapbid.mapper;

import com.tech.snapbid.dto.AuctionItemRequestDto;
import com.tech.snapbid.dto.AuctionItemResponseDto;
import com.tech.snapbid.models.AuctionItem;
import com.tech.snapbid.models.User;

public class AuctionItemMapper {

    public static AuctionItem fromDto(AuctionItemRequestDto dto, User seller) {
        AuctionItem item = new AuctionItem();
        item.setTitle(dto.getTitle());
        item.setDescription(dto.getDescription());
        item.setStartingPrice(dto.getStartingPrice());
        item.setStartTime(dto.getStartTime());
        item.setEndTime(dto.getEndTime());
        item.setSeller(seller);
        return item;
    }

    public static AuctionItemResponseDto toDto(AuctionItem item) {
        AuctionItemResponseDto dto = new AuctionItemResponseDto();
        dto.setId(item.getId());
        dto.setTitle(item.getTitle());
        dto.setDescription(item.getDescription());
        dto.setStartingPrice(item.getStartingPrice());
        dto.setStartTime(item.getStartTime());
        dto.setEndTime(item.getEndTime());
        dto.setSellerUsername(item.getSeller().getUsername());
        dto.setCreatedAt(item.getCreatedAt());
        dto.setUpdatedAt(item.getUpdatedAt());
        return dto;
    }
}

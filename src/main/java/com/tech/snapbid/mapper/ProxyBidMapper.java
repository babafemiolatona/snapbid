package com.tech.snapbid.mapper;

import com.tech.snapbid.dto.ProxyBidResponseDto;
import com.tech.snapbid.models.ProxyBid;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProxyBidMapper {

    public static ProxyBidResponseDto toDto(ProxyBid p) {
        if (p == null) return null;
        ProxyBidResponseDto dto = new ProxyBidResponseDto();
        dto.setId(p.getId());
        dto.setAuctionId(p.getAuctionItem() != null ? p.getAuctionItem().getId() : null);
        dto.setMaxAmount(p.getMaxAmount());
        dto.setActive(p.isActive());
        dto.setCreatedAt(p.getCreatedAt());
        return dto;
    }
}
package com.tech.snapbid.mapper;

import com.tech.snapbid.dto.BidResponseDto;
import com.tech.snapbid.models.Bid;

public class BidMapper {

    public static BidResponseDto mapToDto(Bid bid) {
        BidResponseDto dto = new BidResponseDto();
        dto.setId(bid.getId());
        dto.setAmount(bid.getAmount());
        dto.setBidderUsername(bid.getBidder().getUsername());
        dto.setCreatedAt(bid.getCreatedAt());
        dto.setUpdatedAt(bid.getUpdatedAt());
        return dto;
    }
}

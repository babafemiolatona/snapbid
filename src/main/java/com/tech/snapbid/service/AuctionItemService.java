package com.tech.snapbid.service;


import org.springframework.data.domain.Page;

import com.tech.snapbid.dto.ApiResponse;
import com.tech.snapbid.dto.AuctionItemRequestDto;
import com.tech.snapbid.dto.AuctionItemResponseDto;
import com.tech.snapbid.dto.AuctionItemUpdateDto;
import com.tech.snapbid.models.User;

public interface AuctionItemService {

    AuctionItemResponseDto createAuctionItem(User seller, AuctionItemRequestDto dto);
    AuctionItemResponseDto updateAuctionItem(User seller, Long id, AuctionItemUpdateDto dto);
    ApiResponse deleteAuctionItem(User seller, Long id);
    Page<AuctionItemResponseDto> getAllAuctionItemsBySeller(User seller, int page, int size);
    AuctionItemResponseDto getAuctionItemById(User seller, Long id);

}

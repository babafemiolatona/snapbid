package com.tech.snapbid.service;

import com.tech.snapbid.dto.AuctionItemResponseDto;
import com.tech.snapbid.mapper.AuctionItemMapper;
import com.tech.snapbid.models.AuctionItem;
import com.tech.snapbid.models.AuctionStatus;
import com.tech.snapbid.repository.AuctionItemRepository;
import com.tech.snapbid.spec.AuctionItemSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.tech.snapbid.spec.AuctionItemSpecifications.*;

@Service
@RequiredArgsConstructor
public class AuctionSearchServiceImpl implements AuctionSearchService {

    private final AuctionItemRepository repo;

    @Override
    public Page<AuctionItemResponseDto> search(String q,
                                               AuctionStatus status,
                                               BigDecimal minPrice,
                                               BigDecimal maxPrice,
                                               LocalDateTime endingBefore,
                                               LocalDateTime endingAfter,
                                               Pageable pageable) {

        var spec = AuctionItemSpecifications.and(
            text(q),
            status(status),
            minPrice(minPrice),
            maxPrice(maxPrice),
            endingBefore(endingBefore),
            endingAfter(endingAfter)
        );

        return repo.findAll(spec, pageable)
            .map(AuctionItemMapper::toDto);
    }
}
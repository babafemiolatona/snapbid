package com.tech.snapbid.spec;

import com.tech.snapbid.models.AuctionItem;
import com.tech.snapbid.models.AuctionStatus;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class AuctionItemSpecifications {

    private AuctionItemSpecifications() {}

    public static Specification<AuctionItem> text(String q) {
        if (q == null || q.isBlank()) return null;
        String like = "%" + q.trim().toLowerCase() + "%";
        return (root, cq, cb) -> cb.or(
            cb.like(cb.lower(root.get("title")), like),
            cb.like(cb.lower(root.get("description")), like)
        );
    }

    public static Specification<AuctionItem> status(AuctionStatus status) {
        return status == null ? null : (r,cq,cb) -> cb.equal(r.get("status"), status);
    }

    public static Specification<AuctionItem> minPrice(BigDecimal min) {
        return min == null ? null : (r,cq,cb) -> cb.ge(r.get("startingPrice"), min);
    }

    public static Specification<AuctionItem> maxPrice(BigDecimal max) {
        return max == null ? null : (r,cq,cb) -> cb.le(r.get("startingPrice"), max);
    }

    public static Specification<AuctionItem> endingAfter(LocalDateTime after) {
        return after == null ? null : (r,cq,cb) -> cb.greaterThan(r.get("endTime"), after);
    }

    public static Specification<AuctionItem> endingBefore(LocalDateTime before) {
        return before == null ? null : (r,cq,cb) -> cb.lessThan(r.get("endTime"), before);
    }

    public static Specification<AuctionItem> and(Specification<AuctionItem>... specs) {
        Specification<AuctionItem> result = Specification.where(null);
        for (Specification<AuctionItem> s : specs) {
            if (s != null) result = result.and(s);
        }
        return result;
    }
}
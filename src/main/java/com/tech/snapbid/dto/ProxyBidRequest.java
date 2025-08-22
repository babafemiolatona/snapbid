package com.tech.snapbid.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProxyBidRequest {
    @NotNull(message = "Maximum bid amount is required")
    @DecimalMin(value = "0.01", message = "Maximum bid must be greater than 0")
    private BigDecimal maxAmount;
}
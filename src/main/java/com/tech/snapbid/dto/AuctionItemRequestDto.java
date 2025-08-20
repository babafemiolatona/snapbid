package com.tech.snapbid.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AuctionItemRequestDto {

    @NotBlank(message = "Title is required.")
    private String title;

    @NotBlank(message = "Description is required.")
    private String description;

    @NotNull(message = "Starting price is required.")
    @DecimalMin(value = "0.0", inclusive = false, message = "Starting price must be greater than 0.")
    private BigDecimal startingPrice;

    @NotNull(message = "Start time is required.")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required.")
    private LocalDateTime endTime;

    @AssertTrue(message = "endTime must be after startTime")
    @JsonIgnore
    @Schema(hidden = true)
    public boolean isEndAfterStart() {
        return startTime != null && endTime != null && endTime.isAfter(startTime);
    }

}

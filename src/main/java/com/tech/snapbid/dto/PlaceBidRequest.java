package com.tech.snapbid.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PlaceBidRequest {

    @NotNull
    private Double amount;

}

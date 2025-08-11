
package com.tech.snapbid.models;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Role {
    SELLER,
    BIDDER,
    ADMIN;

    @JsonCreator
    public static Role fromString(String value) {
        if (value == null) throw new IllegalArgumentException("Role cannot be null");
        return Role.valueOf(value.trim().toUpperCase());
    }
}

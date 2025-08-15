package com.tech.snapbid.exceptions;

public class AuctionCancelledException extends RuntimeException {
    
    public AuctionCancelledException(String message) {
        super(message);
    }
}

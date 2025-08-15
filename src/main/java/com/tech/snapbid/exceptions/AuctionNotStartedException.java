package com.tech.snapbid.exceptions;

public class AuctionNotStartedException extends RuntimeException {

    public AuctionNotStartedException(String message) {
        super(message);
    }
}
package com.tech.snapbid.exceptions;

public class BidTooLowException extends RuntimeException {
    
    public BidTooLowException(String message) {
        super(message);
    }
}

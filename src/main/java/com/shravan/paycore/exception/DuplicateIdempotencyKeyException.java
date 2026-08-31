package com.shravan.paycore.exception;

public class DuplicateIdempotencyKeyException extends RuntimeException {

    public DuplicateIdempotencyKeyException(String message) {
        super(message);
    }
}
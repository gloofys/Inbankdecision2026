package com.inbank.decision2026.exceptions;

public class InvalidPersonalCodeException extends RuntimeException {
    public InvalidPersonalCodeException(String message) {
        super(message);
    }
}
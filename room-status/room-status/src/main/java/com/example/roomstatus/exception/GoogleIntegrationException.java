package com.example.roomstatus.exception;

public class GoogleIntegrationException extends RuntimeException {

    private final ErrorCode errorCode;

    public GoogleIntegrationException(String message) {
        super(message);
        this.errorCode = ErrorCode.GOOGLE_SERVICE_UNAVAILABLE;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
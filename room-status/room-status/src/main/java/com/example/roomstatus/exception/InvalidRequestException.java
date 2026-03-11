package com.example.roomstatus.exception;

public class InvalidRequestException extends RuntimeException {

    private final ErrorCode errorCode;

    public InvalidRequestException(String message) {
        super(message);
        this.errorCode = ErrorCode.INVALID_REQUEST;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
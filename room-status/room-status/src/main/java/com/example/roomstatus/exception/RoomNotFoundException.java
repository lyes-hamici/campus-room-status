package com.example.roomstatus.exception;

public class RoomNotFoundException extends RuntimeException {

    private final ErrorCode errorCode;

    public RoomNotFoundException(String message) {
        super(message);
        this.errorCode = ErrorCode.ROOM_NOT_FOUND;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
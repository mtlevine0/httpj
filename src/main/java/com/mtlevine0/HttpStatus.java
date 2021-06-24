package com.mtlevine0;

public enum HttpStatus {
    OK(200, "Ok"),
    BAD_REQUEST(400, "Bad Request"),
    NOT_FOUND(404, "Not Found");

    private final int value;
    private final String reason;

     HttpStatus(int value, String reason) {
        this.value = value;
        this.reason = reason;
    }

    public int getValue() {
        return value;
    }

    public String getReason() {
        return reason;
    }
}

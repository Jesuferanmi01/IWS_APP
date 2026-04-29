package com.understandingjava.iws_app.Execeptions;

public class RateLimitExceededException extends RuntimeException {

    private final String ipAddress;

    public RateLimitExceededException(String ipAddress) {
        super("Too many requests from IP: " + ipAddress + ". Limit is 5 per minute.");
        this.ipAddress = ipAddress;
    }

    public String getIpAddress() {
        return ipAddress;
    }
}
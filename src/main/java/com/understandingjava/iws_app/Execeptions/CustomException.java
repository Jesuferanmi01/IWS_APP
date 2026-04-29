package com.understandingjava.iws_app.Execeptions;

public class CustomException extends RuntimeException {

    public CustomException(String message) { super(message); }
    public CustomException(String message, Throwable cause) { super(message, cause); }


    public static class ValidationException extends CustomException {
        public ValidationException(String message) { super(message); }
    }


    public static class NotFoundException extends CustomException {
        public NotFoundException(String message) { super(message); }
    }


    public static class DatabaseException extends CustomException {
        public DatabaseException(String message, Throwable cause) { super(message, cause); }
    }


    public static class SomethingWentWrongException extends CustomException {
        public SomethingWentWrongException(String message) { super(message); }
        public SomethingWentWrongException(String message, Throwable cause) { super(message, cause); }
    }


    public static class UnauthorizedException extends CustomException {
        public UnauthorizedException(String message) { super(message); }
    }

    public static class RateLimitExceededException extends CustomException {
        public RateLimitExceededException(String message) { super(message); }
    }

}



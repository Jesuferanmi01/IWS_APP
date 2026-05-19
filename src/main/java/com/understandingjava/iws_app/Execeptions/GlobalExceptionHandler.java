package com.understandingjava.iws_app.Execeptions;

import com.understandingjava.iws_app.Util.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<ApiResponse<Void>> handleValidationErrors(MethodArgumentNotValidException ex) {
//        String errors = ex.getBindingResult().getFieldErrors().stream()
//                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
//                .collect(Collectors.joining(", "));
//        log.warn("[VALIDATION] {}", errors);
//        return ResponseEntity.badRequest().body(ApiResponse.error(errors));
//    }
//
//    @ExceptionHandler(CustomException.ValidationException.class)
//    public ResponseEntity<ApiResponse<Void>> handleValidation(CustomException.ValidationException ex) {
//        log.warn("[VALIDATION] {}", ex.getMessage());
//        return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
//    }
//
//    @ExceptionHandler(CustomException.NotFoundException.class)
//    public ResponseEntity<ApiResponse<Void>> handleNotFound(CustomException.NotFoundException ex) {
//        log.warn("[NOT FOUND] {}", ex.getMessage());
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(ex.getMessage()));
//    }
//
//    @ExceptionHandler(CustomException.UnauthorizedException.class)
//    public ResponseEntity<ApiResponse<Void>> handleUnauthorized(CustomException.UnauthorizedException ex) {
//        log.warn("[UNAUTHORIZED] {}", ex.getMessage());
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(ex.getMessage()));
//    }
//
//    @ExceptionHandler(CustomException.DatabaseException.class)
//    public ResponseEntity<ApiResponse<Void>> handleDatabase(CustomException.DatabaseException ex) {
//        log.error("[DATABASE ERROR] {}", ex.getMessage(), ex.getCause());
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(ex.getMessage()));
//    }
//
//    @ExceptionHandler(CustomException.SomethingWentWrongException.class)
//    public ResponseEntity<ApiResponse<Object>> handleSomethingWentWrong(CustomException.SomethingWentWrongException ex) {
//        log.error("[INTERNAL ERROR] {}", ex.getMessage(), ex.getCause());
//        return ResponseEntity.status(444).body(ApiResponse.error(ex.getMessage()));
//    }
//
//
//    @ExceptionHandler(RateLimitExceededException.class)
//    public ResponseEntity<ApiResponse<Void>> handleRateLimit(RateLimitExceededException ex) {
//        log.warn("[RATE_LIMIT] IP blocked — {}", ex.getIpAddress());
//        return ResponseEntity
//                .status(HttpStatus.TOO_MANY_REQUESTS)
//                .body(ApiResponse.error(ex.getMessage()));
//    }
//
//    @ExceptionHandler(NoResourceFoundException.class)
//    public ResponseEntity<Void> handleNoResourceFound(NoResourceFoundException ex) {
//        return ResponseEntity.notFound().build();
//    }
//
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ApiResponse<Void>> handleGeneric(Exception ex) {
//        log.error("[UNEXPECTED] {}", ex.getMessage(), ex);
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(ApiResponse.error("Something went wrong. Please try again later."));
//    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationErrors(
            MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.warn("[VALIDATION] {}", errors);
        return ResponseEntity.badRequest().body(ApiResponse.error(errors));
    }

    @ExceptionHandler(CustomException.ValidationException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(
            CustomException.ValidationException ex) {
        log.warn("[VALIDATION] {}", ex.getMessage());
        return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(CustomException.NotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(
            CustomException.NotFoundException ex) {
        log.warn("[NOT FOUND] {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(CustomException.UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnauthorized(
            CustomException.UnauthorizedException ex) {
        log.warn("[UNAUTHORIZED] {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(CustomException.DatabaseException.class)
    public ResponseEntity<ApiResponse<Void>> handleDatabase(
            CustomException.DatabaseException ex) {
        log.error("[DATABASE ERROR] {}", ex.getMessage(), ex.getCause());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(CustomException.SomethingWentWrongException.class)
    public ResponseEntity<ApiResponse<Void>> handleSomethingWentWrong(
            CustomException.SomethingWentWrongException ex) {
        log.error("[INTERNAL ERROR] {}", ex.getMessage(), ex.getCause());
        return ResponseEntity.status(444).body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ApiResponse<Void>> handleRateLimit(
            RateLimitExceededException ex) {
        log.warn("[RATE_LIMIT] IP blocked — {}", ex.getIpAddress());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Void> handleNoResourceFound(NoResourceFoundException ex) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneric(Exception ex) {
        log.error("[UNEXPECTED] {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Something went wrong. Please try again later."));
    }
}
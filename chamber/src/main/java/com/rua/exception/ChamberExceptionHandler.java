package com.rua.exception;

import feign.FeignException;
import feign.RetryableException;
import io.netty.handler.timeout.ReadTimeoutException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.HashMap;
import java.util.Map;

import static com.rua.constant.ChamberConstants.*;

@RestControllerAdvice
public class ChamberExceptionHandler {

    // From AuthenticationEntryPoint
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<String> handleAuthenticationException(AuthenticationException e) {
        final var errorMessage = ERROR_AUTHENTICATION_FAILED + e.getMessage();
        return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
    }

    // During registration, if username already exists, this exception will be thrown
    @ExceptionHandler(ChamberConflictUsernameException.class)
    public ResponseEntity<String> handleConflictUsernameException(ChamberConflictUsernameException e) {
        final var errorMessage = ERROR_CONFLICT_USERNAME + e.getMessage();
        return new ResponseEntity<>(errorMessage, HttpStatus.CONFLICT);
    }

    // For chat completion without stream, Feign throw this exception if messages are too long
    @ExceptionHandler(FeignException.BadRequest.class)
    public ResponseEntity<String> handleFeignBadRequestException(FeignException.BadRequest e) {
        final var errorMessage = ERROR_MESSAGES_TOO_LONG + e.getMessage();
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    // For chat completion without stream, Feign usually throw this exception on timeout
    @ExceptionHandler(RetryableException.class)
    public ResponseEntity<String> handleFeignReadTimeoutException(RetryableException e) {
        final var errorMessage = ERROR_NO_STREAM_READ_TIMEOUT + e.getMessage();
        return new ResponseEntity<>(errorMessage, HttpStatus.REQUEST_TIMEOUT);
    }

    // For (chat) completion with stream, Webclient usually throw this exception if prompt is too long
    @ExceptionHandler(WebClientResponseException.BadRequest.class)
    public ResponseEntity<String> handleWebClientBadRequestException(WebClientResponseException.BadRequest e) {
        final var errorMessage = ERROR_PROMPT_TOO_LONG + e.getMessage();
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    // Webclient wraps ReadTimeoutException in WebClientRequestException
    @ExceptionHandler(WebClientRequestException.class)
    public ResponseEntity<String> handleWebClientRequestException(WebClientRequestException e) {
        if (e.getCause() instanceof ReadTimeoutException readTimeoutException) {
            final var errorMessage = ERROR_STREAM_READ_TIMEOUT + readTimeoutException.getMessage();
            return new ResponseEntity<>(errorMessage, HttpStatus.GATEWAY_TIMEOUT);
        } else {
            return handleException(e);
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException e) {
        Map<String, String> invalidFields = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            invalidFields.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(invalidFields, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    // All Other exceptions will be handled by this method
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        final var errorMessage = ERROR_UNKNOWN_EXCEPTION + e.getMessage();
        return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
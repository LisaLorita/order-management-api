package io.github.lisalorita.ordermanagement.shared.api;

import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import io.github.lisalorita.ordermanagement.users.exceptions.EmailAlreadyExists;
import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(EmailAlreadyExists.class)
        public ResponseEntity<ApiErrorResponse> handleEmailAlreadyExists(
                        EmailAlreadyExists ex,
                        HttpServletRequest request) {
                HttpStatus status = HttpStatus.CONFLICT;

                ApiErrorResponse body = new ApiErrorResponse(
                                Instant.now(),
                                status.value(),
                                status.getReasonPhrase(),
                                "EMAIL_ALREADY_EXISTS",
                                ex.getMessage(),
                                request.getRequestURI(),
                                List.of());

                return ResponseEntity.status(status).body(body);
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiErrorResponse> handleValidation(
                        MethodArgumentNotValidException ex,
                        HttpServletRequest request) {
                HttpStatus status = HttpStatus.BAD_REQUEST;

                List<ApiFieldError> fieldErrors = ex.getBindingResult()
                                .getFieldErrors()
                                .stream()
                                .map(this::toApiFieldError)
                                .toList();

                ApiErrorResponse body = new ApiErrorResponse(
                                Instant.now(),
                                status.value(),
                                status.getReasonPhrase(),
                                "VALIDATION_ERROR",
                                "Validation failed",
                                request.getRequestURI(),
                                fieldErrors);

                return ResponseEntity.status(status).body(body);
        }

        private ApiFieldError toApiFieldError(FieldError error) {
                String message = error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid value";
                return new ApiFieldError(error.getField(), message);
        }
}
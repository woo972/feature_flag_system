package com.featureflag.core.controller.advice;

import com.featureflag.shared.web.ValidationErrorResponse;
import com.featureflag.shared.web.Violation;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice(basePackages = "com.featureflag")
public class ValidationExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<ValidationErrorResponse> handleBindingExceptions(BindException exception) {
        List<Violation> violations = collectFieldErrors(exception.getBindingResult());
        return buildErrorResponse("Request validation failed.", violations);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationErrorResponse> handleConstraintViolation(ConstraintViolationException exception) {
        List<Violation> violations = exception.getConstraintViolations().stream()
                .map(this::toViolation)
                .collect(Collectors.toList());
        return buildErrorResponse("Request validation failed.", violations);
    }

    private List<Violation> collectFieldErrors(BindingResult bindingResult) {
        List<Violation> violations = new ArrayList<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            violations.add(Violation.builder()
                    .field(fieldError.getField())
                    .message(fieldError.getDefaultMessage())
                    .build());
        }
        return violations;
    }

    private Violation toViolation(ConstraintViolation<?> violation) {
        return Violation.builder()
                .field(violation.getPropertyPath().toString())
                .message(violation.getMessage())
                .build();
    }

    private ResponseEntity<ValidationErrorResponse> buildErrorResponse(String message, List<Violation> violations) {
        ValidationErrorResponse body = ValidationErrorResponse.builder()
                .timestamp(Instant.now())
                .message(message)
                .violations(violations)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}

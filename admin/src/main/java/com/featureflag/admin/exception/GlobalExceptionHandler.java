package com.featureflag.admin.exception;

import com.featureflag.shared.exception.FeatureFlagNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FeatureFlagNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public String handleFeatureFlagNotFoundException(HttpServletRequest request, FeatureFlagNotFoundException ex, Model model) {
        log.error("FeatureFlagNotFoundException: {}", ex.getMessage());

        model.addAttribute("status", HttpStatus.NOT_FOUND.value());
        model.addAttribute("error", HttpStatus.NOT_FOUND.getReasonPhrase());
        model.addAttribute("message", ex.getMessage());
        model.addAttribute("path", request.getRequestURI());

        return "error/error";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleException(HttpServletRequest request, Exception ex, Model model) {
        log.error("Exception: {}", ex.getMessage());
        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("error", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        model.addAttribute("message", ex.getMessage());
        model.addAttribute("path", request.getRequestURI());

        return "error/500";
    }



}

package ru.grigorii.calendar_reminder_system.controller.advice;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.stream.Collectors;

/**
 * Обработчик ошибкок REST API
 */
@RestControllerAdvice(basePackages = "ru.grigorii.calendar_reminder_system.controller.rest")
public class RestExceptionHandler
{

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(
            EntityNotFoundException ex,
            HttpServletRequest request
    )
    {
        return new ApiError(
                404,
                "NOT_FOUND",
                ex.getMessage(),
                request.getRequestURI(),
                Instant.now()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    )
    {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining("; "));

        return new ApiError(
                400,
                "VALIDATION_ERROR",
                message,
                request.getRequestURI(),
                Instant.now()
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request
    )
    {
        return new ApiError(
                403,
                "FORBIDDEN",
                "Access denied",
                request.getRequestURI(),
                Instant.now()
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleAny(
            Exception ex,
            HttpServletRequest request
    )
    {
        return new ApiError(
                500,
                "INTERNAL_ERROR",
                "Unexpected error",
                request.getRequestURI(),
                Instant.now()
        );
    }

}

package com.himanshu.jobflow.exception;

import com.himanshu.jobflow.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleEmailAlreadyExist(EmailAlreadyExistsException exception) {
        return new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                exception.getMessage()
        );
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleInvalidCredentials(InvalidCredentialsException exception) {
        return new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                exception.getMessage()
        );
    }

    @ExceptionHandler(ApplicationNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleApplicationNotFound(ApplicationNotFoundException exception) {
        return new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                exception.getMessage()
        );
    }

    @ExceptionHandler(UnauthorizedApplicationException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleUnauthorizedApplication(UnauthorizedApplicationException exception) {
        return new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                exception.getMessage()
        );
    }

    @ExceptionHandler(InvalidStatusException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidStatus(InvalidStatusException exception) {
        return new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                exception.getMessage()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(
            MethodArgumentNotValidException exception) {

        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> {
                    if (error.getField().equals("password")) {
                        return "Password must be between 8 and 100 characters.";
                    }

                    return error.getField() + ": " + error.getDefaultMessage();
                })
                .collect(Collectors.joining(", "));

        return new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                message
        );
    }

    @ExceptionHandler(ResumeNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleResumeNotFound(
            ResumeNotFoundException exception) {

        return new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                exception.getMessage()
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericException(Exception exception) {
        return new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred"
        );
    }

}

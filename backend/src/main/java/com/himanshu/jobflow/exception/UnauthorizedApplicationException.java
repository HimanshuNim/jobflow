package com.himanshu.jobflow.exception;

public class UnauthorizedApplicationException extends RuntimeException {
    public UnauthorizedApplicationException(String message) {
        super(message);
    }
}

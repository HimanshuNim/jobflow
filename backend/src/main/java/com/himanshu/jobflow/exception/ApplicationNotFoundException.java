package com.himanshu.jobflow.exception;

public class ApplicationNotFoundException extends RuntimeException {
    public ApplicationNotFoundException(String message) {
        super(message);
    }
}

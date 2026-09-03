package com.himanshu.jobflow.exception;

public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(String message){
        super(message);
    }
}

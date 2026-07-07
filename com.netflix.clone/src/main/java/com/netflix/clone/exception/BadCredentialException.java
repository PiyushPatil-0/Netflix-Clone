package com.netflix.clone.exception;

public class BadCredentialException extends RuntimeException{
    public BadCredentialException(String message){
        super(message);
    }
}

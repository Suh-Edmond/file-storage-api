package com.learningmanagementsystem.FileService.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class CustomIOException extends  RuntimeException{
    public CustomIOException(String message) {
        super(message);
    }
}

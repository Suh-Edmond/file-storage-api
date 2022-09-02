package com.learningmanagementsystem.FileService.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MethodArgumentNotValidExceptionResponse {

    private String message;
    private HashMap<String, String> errors;
}

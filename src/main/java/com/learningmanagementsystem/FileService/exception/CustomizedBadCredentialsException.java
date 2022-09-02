package com.learningmanagementsystem.FileService.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CustomizedBadCredentialsException  extends  RuntimeException{

	public CustomizedBadCredentialsException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	
}

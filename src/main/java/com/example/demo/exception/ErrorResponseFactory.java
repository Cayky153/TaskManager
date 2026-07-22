package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ErrorResponseFactory {

    public static ResponseEntity<ErrorResponse> validationError(String message) {
    	 ErrorResponse response = new ValidationErrorResponse(message);
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    public static ResponseEntity<ErrorResponse> runtimeError(String message) {
    	   ErrorResponse response = new RuntimeErrorResponse(message);
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
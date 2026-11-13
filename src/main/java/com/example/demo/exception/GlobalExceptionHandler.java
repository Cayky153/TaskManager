package com.example.demo.exception;


import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
	    Map<String, String> errors = new HashMap<>();
	    ex.getBindingResult().getFieldErrors().forEach(
	        error -> errors.put(error.getField(), error.getDefaultMessage())
	    );
	    return ErrorResponseFactory.validationError(errors.toString());
	}

	@ExceptionHandler(TarefaNaoEncontradaException.class)
	public ResponseEntity<ErrorResponse> handleNotFound(TarefaNaoEncontradaException ex) {
	    return ErrorResponseFactory.notFoundError(ex.getMessage());
	}
	
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ErrorResponse> handleRuntime(RuntimeException ex) {
	    return ErrorResponseFactory.runtimeError(ex.getMessage());
	}
	
}

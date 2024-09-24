package com.in.compliance.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.in.compliance.dto.ErrorDetails;

@ControllerAdvice
public class GlobalExceptionHandler {

	/*
	 * @ExceptionHandler(ResourceNotFoundException.class) public ResponseEntity<?>
	 * handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest
	 * request) { ErrorDetails errorDetails = new
	 * ErrorDetails(HttpStatus.NOT_FOUND.value(), ex.getMessage(),
	 * request.getDescription(false)); return new ResponseEntity<>(errorDetails,
	 * HttpStatus.NOT_FOUND); }
	 */

	@ExceptionHandler(JwtTokenException.class)
	public ResponseEntity<String> handleJwtTokenExpiredException(JwtTokenException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
	}

	// Handle global exceptions
	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> handleGlobalException(Exception ex, WebRequest request) {
		ErrorDetails errorDetails = new ErrorDetails(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(),
				request.getDescription(false));
		return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	
}

package com.steel.product.application.aop;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import com.steel.product.application.exception.ApiError;
import com.steel.product.application.exception.MockException;

@RestControllerAdvice
public class CustomErrorHandler {

	@ExceptionHandler(MockException.class)
	public ResponseEntity<ApiError> handleMock(MockException ex) {
		ApiError error = ApiError.builder().code(ex.getCode()).message(ex.getMessage()).errors(ex.getErrors()).build();
		return ResponseEntity.badRequest().body(error);
	}

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<ApiError> handleStatus(ResponseStatusException ex) {

		ApiError error = ApiError.builder().code(String.valueOf(ex.getStatus().value())).message(ex.getReason())
				.build();

		return ResponseEntity.status(ex.getStatus()).header("Content-Type", "application/json").body(error);
	}

}

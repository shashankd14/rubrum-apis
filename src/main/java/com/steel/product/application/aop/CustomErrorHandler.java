package com.steel.product.application.aop;

import java.io.IOException;

import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;

import com.steel.product.application.exception.ApiError;
import com.steel.product.application.exception.MockException;

@ControllerAdvice
public class CustomErrorHandler {

	@Autowired
	MessageSource messageSource;
	
    @ExceptionHandler(ConstraintViolationException.class)
    public void handleConstraintViolationException(ConstraintViolationException exception,
            ServletWebRequest webRequest) throws IOException {
        webRequest.getResponse().sendError(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
    }
    
	@ExceptionHandler(MockException.class)
	public ResponseEntity<Object> hanldeCDBExpcetion(MockException ex, ServletWebRequest request) throws IOException {

		ApiError apiError = null;
		if (null != ex.getErrors() && !ex.getErrors().isEmpty()) {
			apiError = ApiError.builder().code(ex.getCode()).message(ex.getErrors().get(0)).build();
		} else {

			String msg = messageSource.getMessage(ex.getCode(), ex.getArgs(), request.getLocale());
			apiError = ApiError.builder().code(ex.getCode()).message(msg).errors(ex.getErrors()).build();
			if (StringUtils.isEmpty(apiError.getMessage())) {
				apiError.setMessage(messageSource.getMessage(ex.getCode(), null, request.getLocale()));
			}

		}
		return new ResponseEntity<Object>(apiError, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
    @ExceptionHandler(Exception.class)
    public void hanldeExpcetion(Exception exception,ServletWebRequest webRequest) throws IOException {
    	exception.printStackTrace();
    	webRequest.getResponse().sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage());
    }
}    
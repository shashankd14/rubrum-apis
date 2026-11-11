package com.steel.product.trading.exception;

import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@RestControllerAdvice(basePackages = "com.steel.product.trading")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TradingExceptionHandler {
    private static final Logger log = LogManager.getLogger(TradingExceptionHandler.class);

    @ExceptionHandler({ HttpMessageNotReadableException.class, MissingServletRequestParameterException.class,
            MissingServletRequestPartException.class, MethodArgumentTypeMismatchException.class, MethodArgumentNotValidException.class })
    public ResponseEntity<Object> badRequest(Exception error, HttpServletRequest request) {
        log.warn("Invalid request to {}: {}", request.getRequestURI(), error.getMessage());
        String message = error instanceof MissingServletRequestParameterException
                ? "Missing required field: " + ((MissingServletRequestParameterException) error).getParameterName()
                : error instanceof MissingServletRequestPartException
                        ? "Missing required field: " + ((MissingServletRequestPartException) error).getRequestPartName()
                        : error instanceof MethodArgumentTypeMismatchException
                                ? "Invalid value for " + ((MethodArgumentTypeMismatchException) error).getName()
                                : "The request data is invalid or incomplete. Please check the entered details.";
        return TradingErrors.body(HttpStatus.BAD_REQUEST, message, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> failure(Exception error, HttpServletRequest request) {
        return TradingErrors.failure("complete the request", error, request.getMethod() + " " + request.getRequestURI());
    }
}

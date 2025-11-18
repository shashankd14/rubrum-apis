package com.steel.product.application.exception;

import org.apache.commons.fileupload.FileUploadBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class GlobalExceptionHandler implements HandlerExceptionResolver {

    private static final Logger log = LogManager.getLogger(GlobalExceptionHandler.class);

    private static final long MAX_FILE_SIZE_MB = 3;
    private static final long MAX_REQUEST_SIZE_MB = 20;

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        try {
            if (ex instanceof MaxUploadSizeExceededException) {
                // Check if it's total size > 20MB
                Throwable root = ex;
                while (root.getCause() != null) {
                    root = root.getCause();
                }
                // total > 20MB case
                if (root instanceof FileUploadBase.SizeLimitExceededException) {
                    return writeError(response,
                            HttpStatus.PAYLOAD_TOO_LARGE,
                            "Total upload size cannot exceed " + MAX_REQUEST_SIZE_MB + " MB.");
                }
                // single file > 3MB case
                return writeError(response,
                        HttpStatus.PAYLOAD_TOO_LARGE,
                        "File size cannot exceed " + MAX_FILE_SIZE_MB + " MB.");
            }
        } catch (Exception handlerException) {
            log.error("Exception inside GlobalExceptionHandler...", handlerException);
        }
        return null;
    }
    private ModelAndView writeError(HttpServletResponse response, HttpStatus status, String message) throws Exception {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.getWriter().write("{\"status\":\"fail\",\"message\":\"" + message + "\"}");
        response.getWriter().flush();
        return new ModelAndView();
    }
}

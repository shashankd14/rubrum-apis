/**
 * 
 */
package com.steel.product.application.exception;

import java.util.ArrayList;
import java.util.List;

public class MockException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String code;
    private final String errorType;
    private final List<String> errors;

    // ✅ Full constructor
    public MockException(String code, String message, String errorType, List<String> errors) {
        super(message);
        this.code = code;
        this.errorType = errorType;
        this.errors = errors != null ? errors : new ArrayList<>();
    }

    // ✅ Single message constructor (most common)
    public MockException(String code, String message) {
        super(message);
        this.code = code;
        this.errorType = "BUSINESS";
        this.errors = new ArrayList<>();
        this.errors.add(message);
    }

    // ✅ Multiple errors constructor
    public MockException(String code, List<String> errors) {
        super(errors != null && !errors.isEmpty() ? errors.get(0) : null);
        this.code = code;
        this.errorType = "BUSINESS";
        this.errors = errors != null ? errors : new ArrayList<>();
    }

    public String getCode() {
        return code;
    }

    public String getErrorType() {
        return errorType;
    }

    public List<String> getErrors() {
        return errors;
    }
}

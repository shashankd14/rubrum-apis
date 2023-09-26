/**
 * 
 */
package com.steel.product.application.exception;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Data;

/**
 * @author TCH052
 *
 */
@Data
public class MockException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8086998660062867513L;
	
	private final String code;
	private String errorType;
	private String[] args;
	private  String message;
	private final List<String> errors;
	
	public MockException(String errorCode, String errorType, String[] args) {
		List<String> errors = new ArrayList<>();
		this.args = args;
		this.code = errorCode;
		this.errors = errors;
		this.errorType = errorType;
		this.message = "";
	}

	public MockException(String code, List<String> errors, String errorType, String message) {
		this.code = code;
		this.errors = errors;
		this.errorType = errorType;
		this.message = message;
	}
	
    public MockException(String code, List<String> errors) {
        this.code = code;
        this.errors = errors;
        this.message = "";
        this.errorType = "";
    }
    
    public MockException(String[] errors)
    {
    	this.code = "ADAPTER-001";
    	this.errors = Arrays.asList(errors);
    }
}

/*******************************************************************************
 * Copyright (c) 2016 Techurate Systems Pvt Ltd.
 *
 * All rights reserved.  No part of this work may be reproduced, stored in a retrieval system,
 * adopted or transmitted in any form or by any means, electronic, mechanical, photographic,
 * graphic, optic recording or otherwise, translated in any language or computer language, without
 * the prior written permission of Techurate Systems Pvt Ltd.
 * Techurate Systems Pvt Ltd 
 * 227, 5th Main Rd, Indira Nagar II Stage,
 * Hoysala Nagar, Indiranagar, Bengaluru, 
 * Karnataka 560038, India
 *******************************************************************************/
package com.steel.product.application.exception;

import java.util.ArrayList;
import java.util.List;

import com.steel.product.application.util.ValidationConstants.ErrorType;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BaseException extends Exception {

	private static final long serialVersionUID = 1L;

	private final String code;
	private final String errorType;
	private String[] args;
	private String message;
	private final List<String> errors;
	private final String language;

	public BaseException(String errorCode, String errorType, String[] args, String language) {
		List<String> errors = new ArrayList<>();
		this.args = args;
		this.code = errorCode;
		this.errors = errors;
		this.errorType = errorType;
		this.message = "";
		this.language = language;
	}

	public BaseException(String errorCode, String[] args, List<String> errors, String language) {
		this.args = args;
		this.code = errorCode;
		this.errors = errors;
		this.errorType = ErrorType.INTERNAL_SERVER_ERROR.toString();
		this.message = "";
		this.language = language;
	}

	public BaseException(String code, List<String> errors, String errorType, String message, String language) {
		this.code = code;
		this.errors = errors;
		this.errorType = errorType;
		this.message = message;
		this.language = language;
	}

	public BaseException(String code, List<String> errors, String language) {
		this.code = code;
		this.errors = errors;
		this.message = "";
		this.errorType = "";
		this.language = language;
	}

	public BaseException(String code, String language) {
		this.code = code;
		this.errors = null;
		this.message = "";
		this.errorType = "";
		this.language = language;
	}

	public BaseException(String code, List<String> errors, String language, String errorType) {

		this.language = language;
		this.code = code;
		this.errors = errors;
		this.errorType = errorType;
		this.message = "";
	}

	public BaseException(String language, String code, String errorType, List<String> errors) {
		this.language = language;
		this.code = code;
		this.errorType = errorType;
		this.errors = errors;
		this.message = "";
	}

}
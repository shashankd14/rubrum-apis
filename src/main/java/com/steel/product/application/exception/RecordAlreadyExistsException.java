package com.steel.product.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus( HttpStatus.INTERNAL_SERVER_ERROR )
public class RecordAlreadyExistsException extends RuntimeException
{
    private static final long serialVersionUID = -1662956424220849460L;

    public RecordAlreadyExistsException( String exception )
    {
        super( exception );
    }
}

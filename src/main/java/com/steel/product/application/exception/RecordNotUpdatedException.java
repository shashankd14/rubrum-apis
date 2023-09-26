package com.steel.product.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus( HttpStatus.INTERNAL_SERVER_ERROR )
public class RecordNotUpdatedException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    public RecordNotUpdatedException( String exception )
    {
        super( exception );
    }
}

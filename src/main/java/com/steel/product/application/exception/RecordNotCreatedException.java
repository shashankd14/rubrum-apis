package com.steel.product.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus( HttpStatus.INTERNAL_SERVER_ERROR )
public class RecordNotCreatedException extends RuntimeException
{
    private static final long serialVersionUID = -924464929978516771L;

    public RecordNotCreatedException( String exception )
    {
        super( exception );
    }
}

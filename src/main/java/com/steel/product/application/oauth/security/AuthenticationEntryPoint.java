package com.steel.product.application.oauth.security;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationEntryPoint extends BasicAuthenticationEntryPoint implements Serializable
{

    private static final long serialVersionUID = -7858869558953243875L;

    @Override
    public void commence( HttpServletRequest request, HttpServletResponse response, AuthenticationException authException ) throws IOException
    {
        response.addHeader( "WWW-Authenticate", "Basic realm=" + getRealmName() );
        response.setStatus( HttpServletResponse.SC_UNAUTHORIZED );
        response.sendError( HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized" );

        PrintWriter writer = response.getWriter();
        writer.println( "HTTP Status 401 - " + authException.getMessage() );
    }

    @Override
    public void afterPropertiesSet()
    {
        setRealmName( "STEELPRODUCT-ADMIN" );
        super.afterPropertiesSet();
    }
}

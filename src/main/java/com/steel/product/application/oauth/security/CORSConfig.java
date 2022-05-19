package com.steel.product.application.oauth.security;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CORSConfig
{
    private String allowOrigin = "*";

    @Bean
    public FilterRegistrationBean corsFilter()
    {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials( true );
        corsConfiguration.addAllowedOrigin( allowOrigin );
        corsConfiguration.addAllowedHeader( "Authorization" );
        corsConfiguration.addAllowedHeader( "Content-Type" );
        corsConfiguration.addAllowedHeader( "Accept" );
//        corsConfiguration.addAllowedHeader("Access-Control-Request-Headers");
        corsConfiguration.addAllowedMethod( "POST" );
        corsConfiguration.addAllowedMethod( "GET" );
        corsConfiguration.addAllowedMethod( "DELETE" );
        corsConfiguration.addAllowedMethod( "PUT" );
        corsConfiguration.addAllowedMethod( "OPTIONS" );
        corsConfiguration.setMaxAge( 3600L );
        source.registerCorsConfiguration( "/**", corsConfiguration );

        FilterRegistrationBean bean = new FilterRegistrationBean( new CorsFilter( source ) );
        bean.setOrder( Ordered.HIGHEST_PRECEDENCE );
        return bean;
    }
}

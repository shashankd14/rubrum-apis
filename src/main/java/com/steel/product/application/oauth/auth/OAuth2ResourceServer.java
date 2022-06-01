package com.steel.product.application.oauth.auth;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;

@Configuration
@EnableResourceServer
public class OAuth2ResourceServer extends ResourceServerConfigurerAdapter
{

    private static final String RESOURCE_ID = "resource-server-rest-api";

    @Override
    public void configure( ResourceServerSecurityConfigurer resources )
    {
        resources.resourceId( RESOURCE_ID );
    }

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/swagger-ui.html", 
		                                    "/swagger-resources/**", 
		                                    "/v2/**",
                                            "/webjars/**",            	// swagger-ui webjars
                                            "/swagger-resources/**",  	// swagger-ui resources
                                            "/configuration/**",      	// swagger configuration
                                            "/oauth/**",      	// swagger configuration
                                            "/login/**"      			// login controller
                                            ).permitAll()
                            				//.antMatchers("/**").authenticated() // If we commented this then token is not required to access any service
				.and().exceptionHandling().accessDeniedHandler(new OAuth2AccessDeniedHandler());
	}

}
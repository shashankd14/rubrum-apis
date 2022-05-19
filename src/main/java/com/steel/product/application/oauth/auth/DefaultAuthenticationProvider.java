package com.steel.product.application.oauth.auth;

import java.util.Collections;
import java.util.Optional;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.steel.product.application.dao.UserRepository;
import com.steel.product.application.entity.UserEntity;

public class DefaultAuthenticationProvider implements AuthenticationProvider
{

    private final UserRepository userDetailsRepository;

    public DefaultAuthenticationProvider( UserRepository userDetailsRepository )
    {
        this.userDetailsRepository = userDetailsRepository;
    }

    @Override
    public boolean supports( final Class< ? > authentication )
    {
        return authentication.equals( UsernamePasswordAuthenticationToken.class );
    }

	@Override
	public Authentication authenticate(
			org.springframework.security.core.Authentication authentication) throws AuthenticationException {
		if ( authentication.getName() == null || authentication.getCredentials() == null )
        {
            return null;
        }

        if ( authentication.getName().isEmpty() || authentication.getCredentials().toString().isEmpty() )
        {
            return null;
        }

        final Optional< UserEntity > appUser = this.userDetailsRepository.findByUserName( authentication.getName() );

        if ( appUser.isPresent() )
        {
            final UserEntity user = appUser.get();
            final String userName = authentication.getName();
            final Object providedUserPassword = authentication.getCredentials();

            if ( userName.equalsIgnoreCase( user.getUserName() )
                        && providedUserPassword.equals( user.getPassword() ) )
            {
                return new UsernamePasswordAuthenticationToken( user.getUserName(), user.getPassword(), Collections.singleton( new SimpleGrantedAuthority( user.getRole() ) ) );
            }
        }

        throw new UsernameNotFoundException( "Invalid username or password." );
	}
}

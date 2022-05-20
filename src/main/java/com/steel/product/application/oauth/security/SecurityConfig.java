package com.steel.product.application.oauth.security;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.TokenStoreUserApprovalHandler;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private AuthenticationEntryPoint authenticationEntryPoint;

	@Autowired
	UserDetailsService userDetailsService;

	@Autowired
	ClientDetailsService clientDetailsService;
	@Autowired
	private DataSource dataSource;

	@Autowired
	private DaoAuthenticationProvider authenticationProvider;

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setPasswordEncoder(bCryptPasswordEncoder());
		provider.setUserDetailsService(userDetailsService);
		return provider;
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	public TokenStore tokenStore() {
		return new InMemoryTokenStore();
	}

	@Bean
	@Autowired
	public TokenStoreUserApprovalHandler userApprovalHandler(TokenStore tokenStore) {
		TokenStoreUserApprovalHandler handler = new TokenStoreUserApprovalHandler();
		handler.setTokenStore(tokenStore);
		handler.setRequestFactory(new DefaultOAuth2RequestFactory(clientDetailsService));
		handler.setClientDetailsService(clientDetailsService);
		return handler;
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		// @formatter:off

		auth.authenticationProvider(authenticationProvider).jdbcAuthentication().dataSource(dataSource)
				.usersByUsernameQuery("SELECT USER_NAME,PASSWORD,1 FROM USERS WHERE USER_NAME = ?")
				.authoritiesByUsernameQuery("SELECT USER_NAME, ROLE_ADMIN as role FROM USERS WHERE USER_NAME = ?");

		// @formatter:on
	}

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		// @formatter:off

		// We don't need CSRF
		httpSecurity.csrf().disable()
				// don't create session
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
				// don't authenticate this particular request
				.authorizeRequests().antMatchers("/oauth/**", "/revoke-token", "/login", 
						"/v2/api-docs", // swagger
						"/webjars/**", // swagger-ui webjars
						"/swagger-resources/**", // swagger-ui resources
						"/configuration/**", // swagger configuration
						"/*.html", "/error**")
				.permitAll()
				// all other requests need to be authenticated
				.anyRequest().authenticated().and().httpBasic().and().formLogin().and().
				// make sure we use state less session; session won't be used to store user's
				// state.
				exceptionHandling().authenticationEntryPoint(authenticationEntryPoint);

		// disable page caching
		httpSecurity.headers().cacheControl();

		//httpSecurity.headers().frameOptions().deny();
		//httpSecurity.antMatcher("/public/**").headers().frameOptions().disable();

		httpSecurity.headers().frameOptions().deny().httpStrictTransportSecurity().disable();
		// @formatter:on
	}

}
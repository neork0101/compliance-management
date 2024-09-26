package com.in.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

import com.in.security.jwt.AuthEntryPointJwt;
import com.in.security.jwt.AuthTokenFilter;
import com.in.auth.service.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {
	@Autowired
	UserDetailsServiceImpl userDetailsService;

	@Autowired
	private AuthEntryPointJwt unauthorizedHandler;

	@Bean
	public AuthTokenFilter authenticationJwtTokenFilter() {
		return new AuthTokenFilter();
	}

//  @Override
//  public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
//    authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
//  }

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder());

		return authProvider;
	}

//  @Bean
//  @Override
//  public AuthenticationManager authenticationManagerBean() throws Exception {
//    return super.authenticationManagerBean();
//  }

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable())
				.exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth.requestMatchers("/securitymanagement/api/auth/**").permitAll()
						//.requestMatchers("/securitymanagement/api/vmt/**").permitAll()
						.requestMatchers("/securitymanagement/actuator/**").permitAll()
						.requestMatchers("/securitymanagement/swagger-ui/**").permitAll().anyRequest()
						.authenticated());

		http.authenticationProvider(authenticationProvider());

		http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	/*
	 * @Bean public SecurityFilterChain securityFilterChain(HttpSecurity http)
	 * throws Exception {
	 * 
	 * http // Disable CSRF (useful for stateless APIs, like JWT-based
	 * authentication) .csrf(csrf -> csrf.disable())
	 * 
	 * // Exception handling with custom unauthorized handler
	 * .exceptionHandling(exception -> exception
	 * .authenticationEntryPoint(unauthorizedHandler) )
	 * 
	 * // Stateless session (no session will be created or used by Spring Security)
	 * .sessionManagement(session -> session
	 * .sessionCreationPolicy(SessionCreationPolicy.STATELESS) )
	 * 
	 * // Define authorization rules .authorizeHttpRequests(auth -> auth
	 * .requestMatchers("/compliancemanagement/api/auth/**").permitAll() // Open
	 * access .requestMatchers("/compliancemanagement/api/vmt/**").permitAll() //
	 * Open access .requestMatchers("/compliancemanagement/actuator/**").permitAll()
	 * // Open access to actuator endpoints
	 * .requestMatchers("/compliancemanagement/swagger-ui/**").permitAll() // Open
	 * access to Swagger UI .anyRequest().authenticated() // All other requests
	 * require authentication );
	 * 
	 * // Add security headers http.headers(headers -> headers
	 * .frameOptions(frameOptions -> frameOptions.deny())
	 * .httpStrictTransportSecurity(hsts -> hsts .includeSubDomains(true)
	 * .maxAgeInSeconds(31536000) ) .contentTypeOptions(contentTypeOptions -> {}) //
	 * Prevent MIME-sniffing
	 * 
	 * // Instead of deprecated XSS protection, rely on Content-Security-Policy
	 * .contentSecurityPolicy(csp -> csp
	 * .policyDirectives("default-src 'self'; script-src 'self'; style-src 'self'; img-src 'self';"
	 * ) )
	 * 
	 * .referrerPolicy(referrerPolicy ->
	 * referrerPolicy.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER))
	 * .permissionsPolicy(permissionsPolicy ->
	 * permissionsPolicy.policy("geolocation=(), microphone=(), camera=()")) );
	 * 
	 * return http.build(); }
	 */

}
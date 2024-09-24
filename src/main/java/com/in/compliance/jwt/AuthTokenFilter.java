package com.in.compliance.jwt;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.in.compliance.service.UserDetailsServiceImpl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@Component
public class AuthTokenFilter extends OncePerRequestFilter {
  @Autowired
  private JwtUtils jwtUtils;

  @Autowired
  private UserDetailsServiceImpl userDetailsService;

  private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    try {
      String jwt = parseJwt(request);
           
      if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
        String username = jwtUtils.getUserNameFromJwtToken(jwt);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
            userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
      
		
		/*
		 * } catch (JwtTokenException exp) { throw exp; throw new
		 * AuthenticationException(exp.getMessage())
		 */
    } catch (Exception e) {
      logger.error("Cannot set user authentication: {}", e);
      request.setAttribute("JWT_MSG", e.getMessage());
    }

    filterChain.doFilter(request, response);
  }

  private String parseJwtFromCookies(HttpServletRequest request) {
    String jwt = jwtUtils.getJwtFromCookies(request);
    return jwt;
  }
  
  private String parseJwt(HttpServletRequest request) {
	  String jwt =request.getHeader("Authorization");
	  if(jwt != null && jwt.startsWith("Bearer ")) {
		  jwt=jwt.substring(7);
	  }
	    return jwt;
	  }
}

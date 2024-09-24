package com.in.compliance.jwt;


import java.security.Key;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import com.in.compliance.service.UserDetailsImpl;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtUtils {
   private static final Logger LOG = LoggerFactory.getLogger(JwtUtils.class);


  @Value("${compliance.app.jwtSecret}")
  private String jwtSecret;

  @Value("${compliance.app.jwtExpirationMs}")
  private long jwtExpirationMs;

  @Value("${compliance.app.jwtCookieName}")
  private String jwtCookie;
  


  public String getJwtFromCookies(HttpServletRequest request) {
    Cookie cookie = WebUtils.getCookie(request, jwtCookie);
    if (cookie != null) {
      return cookie.getValue();
    } else {
      return null;
    }
  }

  public ResponseCookie generateJwtCookie(UserDetailsImpl userPrincipal) {
    String jwt = generateTokenFromUsername(userPrincipal.getUsername());
    return ResponseCookie.from(jwtCookie, jwt)
            .path("/api")
            .maxAge(jwtExpirationMs)
            .httpOnly(true)
            .build();

  }

  public String generateToken(UserDetailsImpl userPrincipal) {
	    return  generateTokenFromUsername(userPrincipal.getUsername());
	  }
  
  public ResponseCookie getCleanJwtCookie() {
    ResponseCookie cookie = ResponseCookie.from(jwtCookie, null).path("/api").build();
    return cookie;
  }

  public String getUserNameFromJwtToken(String token) {
    return Jwts.parserBuilder().setSigningKey(key()).build()
        .parseClaimsJws(token).getBody().getSubject();
  }
  
  private Key key() {
    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
  }

  public boolean validateJwtToken(String authToken) {
	  LOG.info("Start Method:validateJwtToken");
    try {
      Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
      return true;
    } catch (MalformedJwtException e) {
    	LOG.error("Invalid JWT token: {}", e.getMessage());
      //throw new JwtTokenException("Invalid JWT token:"+e.getMessage());
    } catch (ExpiredJwtException e) {
    	LOG.error("JWT token is expired: {}", e.getMessage());
     // throw new JwtTokenException("JWT token is expired: "+e.getMessage());
    } catch (UnsupportedJwtException e) {
    	LOG.error("JWT token is unsupported: {}", e.getMessage());
     // throw new JwtTokenException("JWT token is unsupported:"+e.getMessage());
    } catch (IllegalArgumentException e) {
    	LOG.error("JWT claims string is empty: {}", e.getMessage());
      //throw new JwtTokenException("JWT claims string is empty:"+e.getMessage());
    }

   return false;
  }
  
  public String generateTokenFromUsername(String username) {   
	  LOG.info("Start Method:generateTokenFromUsername");
    return Jwts.builder()
              .setSubject(username)
              .setIssuedAt(new Date())
              .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
              .signWith(key(), SignatureAlgorithm.HS256)
              .compact();
  }
}

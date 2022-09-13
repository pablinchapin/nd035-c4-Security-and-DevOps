package com.example.demo.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationVerificationFilter extends BasicAuthenticationFilter {

  @Value("${application.values.header}")
  private String header;

  @Value("${application.values.header.prefix}")
  private String prefix;

  @Value("${application.values.secret}")
  private String secret;

  public JwtAuthenticationVerificationFilter(
      AuthenticationManager authenticationManager) {
    super(authenticationManager);
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain) {
    try{
      String headerValue = request.getHeader(header);

      if(headerValue == null || !headerValue.startsWith(prefix)){
        chain.doFilter(request, response);
      }
      UsernamePasswordAuthenticationToken authenticationToken = getAuthenticationToken(request);

      SecurityContextHolder.getContext().setAuthentication(authenticationToken);
      chain.doFilter(request, response);
    }catch (IOException | ServletException e){
      throw new RuntimeException(e);
    }
  }

  private UsernamePasswordAuthenticationToken getAuthenticationToken(HttpServletRequest request) {
    String token = request.getHeader(header);
    if(token != null){
      String userFromJwt = JWT
          .require(Algorithm.HMAC512(secret)).build()
          .verify(token.replace(new StringBuilder(prefix).append(" ").toString(), ""))
          .getSubject();
      return (userFromJwt != null) ? new UsernamePasswordAuthenticationToken(userFromJwt, null, new ArrayList<>()) : null;
    }
    return null;
  }
}

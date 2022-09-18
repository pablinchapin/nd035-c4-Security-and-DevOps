package com.example.demo.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.demo.config.AppConstantValues;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthenticationVerificationFilter extends BasicAuthenticationFilter {

  public JwtAuthenticationVerificationFilter(
      AuthenticationManager authenticationManager) {
    super(authenticationManager);
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain) {
    try{
      String headerValue = request.getHeader(AppConstantValues.HEADER);

      if(headerValue == null || !headerValue.startsWith(AppConstantValues.PREFIX)){
        chain.doFilter(request, response);
        return;
      }
      UsernamePasswordAuthenticationToken authenticationToken = getAuthenticationToken(request);

      SecurityContextHolder.getContext().setAuthentication(authenticationToken);
      chain.doFilter(request, response);
    }catch (IOException | ServletException e){
      throw new RuntimeException(e);
    }
  }

  private UsernamePasswordAuthenticationToken getAuthenticationToken(HttpServletRequest request) {
    String token = request.getHeader(AppConstantValues.HEADER);
    if(token != null){
      String userFromJwt = JWT
          .require(Algorithm.HMAC512(AppConstantValues.SECRET.getBytes())).build()
          .verify(token.replace(new StringBuilder(AppConstantValues.PREFIX).append(" ").toString(), ""))
          .getSubject();
      return (userFromJwt != null) ? new UsernamePasswordAuthenticationToken(userFromJwt, null, new ArrayList<>()) : null;
    }
    return null;
  }
}

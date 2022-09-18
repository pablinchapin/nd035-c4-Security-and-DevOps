package com.example.demo.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.demo.config.AppConstantValues;
import com.example.demo.model.persistence.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  private final AuthenticationManager authenticationManager;

  public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response) throws AuthenticationException {

    try{
      User user = new ObjectMapper().readValue(request.getInputStream(), User.class);
      return authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), new ArrayList<>())
      );
    }catch (Exception e){
      throw new RuntimeException(e);
    }

  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain, Authentication authResult) {

    String token = JWT.create()
      .withSubject(((org.springframework.security.core.userdetails.User) authResult.getPrincipal()).getUsername())
      .withExpiresAt(new Date(System.currentTimeMillis() + 864_000_000))
      .sign(Algorithm.HMAC512(AppConstantValues.SECRET.getBytes()));

    response.addHeader(AppConstantValues.HEADER, new StringBuilder(AppConstantValues.PREFIX).append(" ").append(token).toString());
  }
}

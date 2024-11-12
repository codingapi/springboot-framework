package com.codingapi.springboot.security.filter;

import com.codingapi.springboot.security.dto.request.LoginRequest;
import com.codingapi.springboot.security.dto.response.LoginResponse;
import com.codingapi.springboot.security.gateway.Token;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public interface SecurityLoginHandler {

    void preHandle(HttpServletRequest request, HttpServletResponse response, LoginRequest loginRequest) throws Exception;

    LoginResponse postHandle(HttpServletRequest request, HttpServletResponse response, LoginRequest loginRequest, UserDetails userDetails, Token token);
}

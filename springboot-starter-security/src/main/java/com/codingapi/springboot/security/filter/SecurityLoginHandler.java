package com.codingapi.springboot.security.filter;

import com.codingapi.springboot.security.dto.request.LoginRequest;
import com.codingapi.springboot.security.dto.response.LoginResponse;
import com.codingapi.springboot.security.gateway.Token;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

public interface SecurityLoginHandler {

    void preHandle(HttpServletRequest request, HttpServletResponse response, LoginRequest loginRequest) throws Exception;

    LoginResponse postHandle(HttpServletRequest request, HttpServletResponse response, LoginRequest loginRequest, UserDetails user, Token token);

}

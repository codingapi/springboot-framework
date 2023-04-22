package com.codingapi.springboot.security.filter;

import com.codingapi.springboot.security.dto.request.LoginRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface SecurityLoginHandler {

    void  preHandle(HttpServletRequest request, HttpServletResponse response, LoginRequest handler) throws Exception;
}

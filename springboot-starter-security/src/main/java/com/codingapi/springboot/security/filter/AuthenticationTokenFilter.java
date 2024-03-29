package com.codingapi.springboot.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthenticationTokenFilter {

    void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain);
}

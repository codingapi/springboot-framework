package com.codingapi.springboot.security.filter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface AuthenticationTokenFilter {


    void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain);

}

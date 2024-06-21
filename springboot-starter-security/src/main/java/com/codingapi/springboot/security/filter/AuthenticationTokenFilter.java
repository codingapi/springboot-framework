package com.codingapi.springboot.security.filter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface AuthenticationTokenFilter {

    void doFilter(HttpServletRequest request, HttpServletResponse response)throws IOException, ServletException;

}

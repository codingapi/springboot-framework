package com.codingapi.springboot.security.filter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface AuthenticationTokenFilter {


    void doFilter(HttpServletRequest request, HttpServletResponse response)throws IOException, ServletException;

}

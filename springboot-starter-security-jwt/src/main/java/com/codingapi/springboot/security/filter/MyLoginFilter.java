package com.codingapi.springboot.security.filter;

import com.alibaba.fastjson.JSONObject;
import com.codingapi.springboot.framework.dto.response.Response;
import com.codingapi.springboot.framework.dto.response.SingleResponse;
import com.codingapi.springboot.security.dto.request.LoginRequest;
import com.codingapi.springboot.security.dto.request.LoginRequestContext;
import com.codingapi.springboot.security.dto.response.LoginResponse;
import com.codingapi.springboot.security.jwt.Jwt;
import com.codingapi.springboot.security.jwt.Token;
import com.codingapi.springboot.security.jwt.TokenContext;
import com.codingapi.springboot.security.properties.SecurityJwtProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Slf4j
public class MyLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final Jwt jwt;

    private final SecurityLoginHandler loginHandler;

    public MyLoginFilter(AuthenticationManager authenticationManager, Jwt jwt, SecurityLoginHandler loginHandler, SecurityJwtProperties securityJwtProperties) {
        super(authenticationManager);
        this.jwt = jwt;
        this.loginHandler = loginHandler;
        this.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(securityJwtProperties.getLoginProcessingUrl(), "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.debug("login authentication ~");
        String content = null;
        try {
            content = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new AuthenticationServiceException("request input stream read fail.");
        }
        LoginRequest login = JSONObject.parseObject(content, LoginRequest.class);
        if (login == null || login.isEmpty()) {
            throw new AuthenticationServiceException("request stream read was null.");
        }
        try {
            loginHandler.preHandle(request,response,login);
        } catch (Exception e) {
            throw new AuthenticationServiceException(e.getLocalizedMessage());
        }
        LoginRequestContext.getInstance().set(login);
        return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword()));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.debug("login success authentication ~");
        User user = (User) authResult.getPrincipal();
        LoginRequest loginRequest = LoginRequestContext.getInstance().get();

        Token token = jwt.create(user.getUsername(), loginRequest.getPassword(),
                user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()),
                TokenContext.getExtra());

        LoginResponse login = new LoginResponse();
        login.setUsername(user.getUsername());
        login.setToken(token.getToken());
        login.setAuthorities(token.getAuthorities());

        String content = JSONObject.toJSONString(SingleResponse.of(login));
        IOUtils.write(content, response.getOutputStream(), StandardCharsets.UTF_8);

        LoginRequestContext.getInstance().clean();
    }


    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.debug("login fail authentication ~");
        String content = JSONObject.toJSONString(Response.buildFailure("login.error", failed.getMessage()));
        IOUtils.write(content, response.getOutputStream(), StandardCharsets.UTF_8);
        LoginRequestContext.getInstance().clean();
    }
}

package com.codingapi.springboot.security.filter;

import com.alibaba.fastjson.JSONObject;
import com.codingapi.springboot.framework.dto.response.Response;
import com.codingapi.springboot.security.exception.TokenExpiredException;
import com.codingapi.springboot.security.jwt.Jwt;
import com.codingapi.springboot.security.jwt.Token;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class MyAuthenticationFilter extends BasicAuthenticationFilter {

    private final static String TOKEN_KEY = "Authorization";

    private final Jwt jwt;

    public MyAuthenticationFilter(AuthenticationManager authenticationManager, Jwt jwt) {
        super(authenticationManager);
        this.jwt = jwt;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.debug("token authentication ~");

        String sign = request.getHeader(TOKEN_KEY);
        if (!StringUtils.hasLength(sign)) {
            writeResponse(response, Response.buildFailure("token.error", "token must not null."));
            return;
        }

        Token token = jwt.parser(sign);
        if (token.canRestToken()) {
            Token newSign = jwt.create(token.getUsername(), token.decodeIv(), token.getAuthorities(),token.getExtra());
            log.info("reset token ");
            response.setHeader(TOKEN_KEY, newSign.getToken());
        }
        try {
            token.verify();
        } catch (TokenExpiredException e) {
            writeResponse(response, Response.buildFailure("token.expire", "token expire."));
            return;
        }

        SecurityContextHolder.getContext().setAuthentication(token.getAuthenticationToken());

        chain.doFilter(request, response);

    }

    private void writeResponse(HttpServletResponse servletResponse, Response returnResponse) throws IOException {
        String content = JSONObject.toJSONString(returnResponse);
        IOUtils.write(content, servletResponse.getOutputStream(), StandardCharsets.UTF_8);
    }
}

package com.codingapi.springboot.security.filter;

import com.alibaba.fastjson.JSONObject;
import com.codingapi.springboot.framework.dto.response.Response;
import com.codingapi.springboot.security.exception.TokenExpiredException;
import com.codingapi.springboot.security.jwt.Jwt;
import com.codingapi.springboot.security.jwt.Token;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationConverter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Slf4j
public class MyAuthenticationFilter extends BasicAuthenticationFilter {

    private final static String TOKEN_KEY = "Authorization";

    private final Jwt jwt;

    private final BasicAuthenticationConverter authenticationConverter = new BasicAuthenticationConverter();

    public MyAuthenticationFilter(AuthenticationManager authenticationManager, Jwt jwt) {
        super(authenticationManager);
        this.jwt = jwt;
    }

    public void setAuthenticationDetailsSource(AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource) {
        this.authenticationConverter.setAuthenticationDetailsSource(authenticationDetailsSource);
    }

    public void setCredentialsCharset(String credentialsCharset) {
        Assert.hasText(credentialsCharset, "credentialsCharset cannot be null or empty");
        this.authenticationConverter.setCredentialsCharset(Charset.forName(credentialsCharset));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.debug("token authentication ~");

        UsernamePasswordAuthenticationToken authRequest = authenticationConverter.convert(request);
        if (authRequest == null) {
            this.logger.trace("Did not process authentication request since failed to find username and password in Basic Authorization header");
            chain.doFilter(request, response);
            return;
        }

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

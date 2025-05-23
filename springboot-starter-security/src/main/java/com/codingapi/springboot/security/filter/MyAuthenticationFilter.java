package com.codingapi.springboot.security.filter;

import com.alibaba.fastjson.JSONObject;
import com.codingapi.springboot.framework.dto.response.Response;
import com.codingapi.springboot.security.exception.TokenExpiredException;
import com.codingapi.springboot.security.gateway.Token;
import com.codingapi.springboot.security.gateway.TokenGateway;
import com.codingapi.springboot.security.properties.CodingApiSecurityProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class MyAuthenticationFilter extends BasicAuthenticationFilter {

    private final static String TOKEN_KEY = "Authorization";

    private final TokenGateway tokenGateway;

    private final CodingApiSecurityProperties securityJwtProperties;
    private final AuthenticationTokenFilter authenticationTokenFilter;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();


    public MyAuthenticationFilter(AuthenticationManager manager, CodingApiSecurityProperties securityJwtProperties, TokenGateway tokenGateway, AuthenticationTokenFilter authenticationTokenFilter) {
        super(manager);
        this.tokenGateway = tokenGateway;
        this.securityJwtProperties = securityJwtProperties;
        this.authenticationTokenFilter = authenticationTokenFilter;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.debug("token authentication ~");
        for (String antUrl : securityJwtProperties.getAuthenticatedUrls()) {
            if (antPathMatcher.match(antUrl, request.getRequestURI())) {

                String sign = request.getHeader(TOKEN_KEY);
                if (!StringUtils.hasLength(sign)) {
                    writeResponse(response, Response.buildFailure("token.error", "token must not null."));
                    return;
                }

                Token token = null;
                try {
                    token = tokenGateway.parser(sign);
                    if (token == null) {
                        writeResponse(response, Response.buildFailure("token.expire", "token expire."));
                        return;
                    }
                    if (token.canRestToken()) {
                        Token newSign = tokenGateway.create(token.getUsername(), token.decodeIv(), token.getAuthorities(), token.getExtra());
                        log.info("reset token ");
                        response.setHeader(TOKEN_KEY, newSign.getToken());
                    }
                    try {
                        token.verify();
                    } catch (TokenExpiredException e) {
                        writeResponse(response, Response.buildFailure("token.expire", "token expire."));
                        return;
                    }
                }catch (Exception e){
                    writeResponse(response, Response.buildFailure("token.expire", "token expire."));
                    return;
                }

                SecurityContextHolder.getContext().setAuthentication(token.getAuthenticationToken());
                authenticationTokenFilter.doFilter(request, response);
            }
        }
        chain.doFilter(request, response);

    }

    private void writeResponse(HttpServletResponse servletResponse, Response returnResponse) throws IOException {
        String content = JSONObject.toJSONString(returnResponse);
        // 设置响应的 Content-Type 为 JSON，并指定字符编码为 UTF-8
        servletResponse.setContentType("application/json;charset=UTF-8");
        servletResponse.setCharacterEncoding("UTF-8");

        IOUtils.write(content, servletResponse.getOutputStream(), StandardCharsets.UTF_8);
    }


}

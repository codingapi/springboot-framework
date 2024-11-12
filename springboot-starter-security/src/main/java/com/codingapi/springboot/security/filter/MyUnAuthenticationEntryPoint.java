package com.codingapi.springboot.security.filter;

import com.alibaba.fastjson.JSONObject;
import com.codingapi.springboot.framework.dto.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class MyUnAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.debug("no authentication ~");
        String content = JSONObject.toJSONString(Response.buildFailure("not.login", "please to login."));
        // 设置响应的 Content-Type 为 JSON，并指定字符编码为 UTF-8
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        IOUtils.write(content, response.getOutputStream(), StandardCharsets.UTF_8);
    }
}

package com.codingapi.springboot.security.filter;

import com.alibaba.fastjson.JSONObject;
import com.codingapi.springboot.framework.dto.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class MyLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.debug("logout success ~");
        String content = JSONObject.toJSONString(Response.buildSuccess());
        // 设置响应的 Content-Type 为 JSON，并指定字符编码为 UTF-8
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        IOUtils.write(content, response.getOutputStream(), StandardCharsets.UTF_8);
    }
}

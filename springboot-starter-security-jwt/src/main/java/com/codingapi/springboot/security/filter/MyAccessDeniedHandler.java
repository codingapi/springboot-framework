package com.codingapi.springboot.security.filter;

import com.alibaba.fastjson.JSONObject;
import com.codingapi.springboot.framework.dto.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class MyAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.debug("access denied");
        String content = JSONObject.toJSONString(Response.buildFailure("not.access", "please check user authentication."));
        IOUtils.write(content, response.getOutputStream(), StandardCharsets.UTF_8);
    }
}

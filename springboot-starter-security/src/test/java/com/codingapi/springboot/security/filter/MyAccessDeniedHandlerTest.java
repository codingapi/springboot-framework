package com.codingapi.springboot.security.filter;

import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * MyAccessDeniedHandler 单元测试
 * <p>
 * 直接调用 handle 方法，验证 403 场景下写出的 JSON 响应内容。
 */
class MyAccessDeniedHandlerTest {

    @Test
    void handleWritesAccessDeniedJsonResponse() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        new MyAccessDeniedHandler().handle(request, response, new AccessDeniedException("denied"));

        assertEquals("application/json;charset=UTF-8", response.getContentType());
        assertEquals("UTF-8", response.getCharacterEncoding());

        JSONObject json = JSONObject.parseObject(response.getContentAsString(StandardCharsets.UTF_8));
        assertEquals("not.access", json.getString("errCode"));
        assertEquals("please check user authentication.", json.getString("errMessage"));
        assertFalse(json.getBooleanValue("success"));
    }

}

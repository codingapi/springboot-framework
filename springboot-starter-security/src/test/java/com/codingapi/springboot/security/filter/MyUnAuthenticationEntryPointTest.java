package com.codingapi.springboot.security.filter;

import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * MyUnAuthenticationEntryPoint 单元测试
 * <p>
 * 直接调用 commence 方法，验证 401 场景下写出的 JSON 响应内容。
 */
class MyUnAuthenticationEntryPointTest {

    @Test
    void commenceWritesNotLoginJsonResponse() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        new MyUnAuthenticationEntryPoint().commence(request, response,
                new InsufficientAuthenticationExceptionForTest("not authenticated"));

        assertEquals("application/json;charset=UTF-8", response.getContentType());
        assertEquals("UTF-8", response.getCharacterEncoding());

        JSONObject json = JSONObject.parseObject(response.getContentAsString(StandardCharsets.UTF_8));
        assertEquals("not.login", json.getString("errCode"));
        assertEquals("please to login.", json.getString("errMessage"));
        assertFalse(json.getBooleanValue("success"));
    }

    /**
     * 简单的 AuthenticationException 实现，用于触发 commence 逻辑
     */
    private static class InsufficientAuthenticationExceptionForTest
            extends org.springframework.security.core.AuthenticationException {

        InsufficientAuthenticationExceptionForTest(String msg) {
            super(msg);
        }
    }

}

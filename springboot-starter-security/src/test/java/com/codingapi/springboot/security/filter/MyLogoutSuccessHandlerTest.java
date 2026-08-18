package com.codingapi.springboot.security.filter;

import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * MyLogoutSuccessHandler 单元测试
 * <p>
 * 直接调用 onLogoutSuccess 方法，验证退出成功后写出的 JSON 响应内容。
 */
class MyLogoutSuccessHandlerTest {

    @Test
    void onLogoutSuccessWritesSuccessJsonResponse() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        new MyLogoutSuccessHandler().onLogoutSuccess(request, response, null);

        assertEquals("application/json;charset=UTF-8", response.getContentType());
        assertEquals("UTF-8", response.getCharacterEncoding());

        JSONObject json = JSONObject.parseObject(response.getContentAsString(StandardCharsets.UTF_8));
        assertTrue(json.getBooleanValue("success"));
    }

}

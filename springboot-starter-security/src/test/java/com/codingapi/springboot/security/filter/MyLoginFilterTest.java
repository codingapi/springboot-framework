package com.codingapi.springboot.security.filter;

import com.codingapi.springboot.security.dto.request.LoginRequest;
import com.codingapi.springboot.security.dto.request.LoginRequestContext;
import com.codingapi.springboot.security.gateway.TokenGateway;
import com.codingapi.springboot.security.properties.CodingApiSecurityProperties;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * MyLoginFilter 单元测试
 * <p>
 * 覆盖 attemptAuthentication 的各异常分支（空请求体、空登录参数、
 * 流读取失败、preHandle 异常）以及正常认证路径。
 */
class MyLoginFilterTest {

    private AuthenticationManager authenticationManager;
    private SecurityLoginHandler loginHandler;
    private MyLoginFilter filter;

    @BeforeEach
    void setUp() {
        authenticationManager = mock(AuthenticationManager.class);
        TokenGateway tokenGateway = mock(TokenGateway.class);
        loginHandler = mock(SecurityLoginHandler.class);
        filter = new MyLoginFilter(authenticationManager, tokenGateway, loginHandler,
                new CodingApiSecurityProperties());
    }

    @AfterEach
    void tearDown() {
        LoginRequestContext.getInstance().clean();
    }

    private MockHttpServletRequest jsonRequest(String body) {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/user/login");
        request.setContent(body.getBytes(StandardCharsets.UTF_8));
        return request;
    }

    @Test
    void attemptAuthenticationWithNullBodyThrowsServiceException() {
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertThrows(AuthenticationServiceException.class,
                () -> filter.attemptAuthentication(jsonRequest("null"), response));
    }

    @Test
    void attemptAuthenticationWithEmptyLoginRequestThrowsServiceException() {
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertThrows(AuthenticationServiceException.class,
                () -> filter.attemptAuthentication(jsonRequest("{}"), response));
    }

    @Test
    void attemptAuthenticationWhenStreamReadFailsThrowsServiceException() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getInputStream()).thenThrow(new IOException("stream read fail"));

        assertThrows(AuthenticationServiceException.class,
                () -> filter.attemptAuthentication(request, new MockHttpServletResponse()));
    }

    @Test
    void attemptAuthenticationWhenPreHandleFailsThrowsServiceException() throws Exception {
        doThrow(new IllegalStateException("pre handle fail"))
                .when(loginHandler).preHandle(any(), any(), any(LoginRequest.class));
        String body = "{\"username\":\"admin\",\"password\":\"123456\"}";

        assertThrows(AuthenticationServiceException.class,
                () -> filter.attemptAuthentication(jsonRequest(body), new MockHttpServletResponse()));
    }

    @Test
    void attemptAuthenticationSuccessDelegatesToAuthenticationManager() {
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        String body = "{\"username\":\"admin\",\"password\":\"123456\"}";

        Authentication result = filter.attemptAuthentication(jsonRequest(body), new MockHttpServletResponse());

        assertSame(authentication, result);

        ArgumentCaptor<UsernamePasswordAuthenticationToken> captor =
                ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(captor.capture());
        assertEquals("admin", captor.getValue().getName());
        assertEquals("123456", captor.getValue().getCredentials());

        // 登录请求被放入线程上下文
        LoginRequest context = LoginRequestContext.getInstance().get();
        assertNotNull(context);
        assertEquals("admin", context.getUsername());
    }

}

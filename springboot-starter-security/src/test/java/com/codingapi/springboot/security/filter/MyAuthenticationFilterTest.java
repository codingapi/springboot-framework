package com.codingapi.springboot.security.filter;

import com.alibaba.fastjson.JSONObject;
import com.codingapi.springboot.security.gateway.Token;
import com.codingapi.springboot.security.gateway.TokenGateway;
import com.codingapi.springboot.security.properties.CodingApiSecurityProperties;
import javax.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * MyAuthenticationFilter 单元测试
 * <p>
 * 通过 mock TokenGateway 覆盖 doFilterInternal 的各分支：
 * 缺少 token、token 解析为 null、解析异常、正常通过、token 重置、过期 token 等。
 */
class MyAuthenticationFilterTest {

    private TokenGateway tokenGateway;
    private AuthenticationTokenFilter authenticationTokenFilter;
    private MyAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        tokenGateway = mock(TokenGateway.class);
        authenticationTokenFilter = mock(AuthenticationTokenFilter.class);
        filter = new MyAuthenticationFilter(mock(AuthenticationManager.class),
                new CodingApiSecurityProperties(), tokenGateway, authenticationTokenFilter);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private JSONObject bodyJson(MockHttpServletResponse response) throws Exception {
        return JSONObject.parseObject(response.getContentAsString(StandardCharsets.UTF_8));
    }

    @Test
    void missingAuthorizationHeaderWritesTokenError() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/hello");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);

        assertEquals("token.error", bodyJson(response).getString("errCode"));
        verify(chain, never()).doFilter(any(), any());
    }

    @Test
    void parserReturnsNullWritesTokenExpire() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/hello");
        request.addHeader("Authorization", "token-value");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);
        when(tokenGateway.parser("token-value")).thenReturn(null);

        filter.doFilterInternal(request, response, chain);

        assertEquals("token.expire", bodyJson(response).getString("errCode"));
        verify(chain, never()).doFilter(any(), any());
    }

    @Test
    void parserThrowsExceptionWritesTokenExpire() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/hello");
        request.addHeader("Authorization", "bad-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);
        when(tokenGateway.parser("bad-token")).thenThrow(new RuntimeException("parser fail"));

        filter.doFilterInternal(request, response, chain);

        assertEquals("token.expire", bodyJson(response).getString("errCode"));
        verify(chain, never()).doFilter(any(), any());
    }

    @Test
    void expiredTokenWritesTokenExpire() throws Exception {
        List<String> authorities = Collections.singletonList("ADMIN");
        Token token = new Token("admin", null, null, authorities, -1000, -1000);
        token.setToken("expired-token");

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/hello");
        request.addHeader("Authorization", "expired-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);
        when(tokenGateway.parser("expired-token")).thenReturn(token);

        filter.doFilterInternal(request, response, chain);

        assertEquals("token.expire", bodyJson(response).getString("errCode"));
        verify(chain, never()).doFilter(any(), any());
    }

    @Test
    void validTokenSetsSecurityContextAndContinuesChain() throws Exception {
        List<String> authorities = Collections.singletonList("ADMIN");
        Token token = new Token("admin", null, "{\"channel\":\"pc\"}", authorities, 900000, 600000);
        token.setToken("token-value");

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/hello");
        request.addHeader("Authorization", "token-value");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);
        when(tokenGateway.parser("token-value")).thenReturn(token);

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(authenticationTokenFilter).doFilter(request, response);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertSame(token, authentication.getPrincipal());
        assertEquals(1, authentication.getAuthorities().size());
    }

    @Test
    void restableTokenTriggersResetAndSetsResponseHeader() throws Exception {
        List<String> authorities = Collections.singletonList("ADMIN");
        // remindTime 已过期而 token 未过期 -> canRestToken() 返回 true
        Token token = new Token("admin", null, null, authorities, 900000, -1000);
        token.setToken("old-token");
        Token newToken = new Token("admin", null, null, authorities, 900000, 600000);
        newToken.setToken("new-token");

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/hello");
        request.addHeader("Authorization", "old-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);
        when(tokenGateway.parser("old-token")).thenReturn(token);
        when(tokenGateway.create(eq("admin"), isNull(), eq(authorities), isNull())).thenReturn(newToken);

        filter.doFilterInternal(request, response, chain);

        assertEquals("new-token", response.getHeader("Authorization"));
        verify(chain).doFilter(request, response);
    }

    @Test
    void notMatchedUrlSkipsTokenCheck() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/open/hello");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(tokenGateway, never()).parser(anyString());
    }

}

package com.codingapi.springboot.security.configurer;

import com.codingapi.springboot.security.properties.CodingApiSecurityProperties;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.config.annotation.web.builders.WebSecurity;

import static org.mockito.Mockito.verify;

/**
 * WebSecurityConfigurer 单元测试
 * <p>
 * 通过 mock WebSecurity 验证 ignoreUrls 被注册到 ignoring 列表。
 */
class WebSecurityConfigurerTest {

    @Test
    void customizeRegistersIgnoreUrls() {
        CodingApiSecurityProperties properties = new CodingApiSecurityProperties();
        properties.setIgnoreUrls("/open/**,/public/**");
        WebSecurityConfigurer configurer = new WebSecurityConfigurer(properties);

        WebSecurity webSecurity = Mockito.mock(WebSecurity.class, Mockito.RETURNS_DEEP_STUBS);
        configurer.customize(webSecurity);

        verify(webSecurity.ignoring()).requestMatchers("/open/**", "/public/**");
    }

}

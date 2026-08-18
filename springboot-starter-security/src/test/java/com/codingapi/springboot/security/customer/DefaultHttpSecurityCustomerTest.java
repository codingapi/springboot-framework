package com.codingapi.springboot.security.customer;

import com.codingapi.springboot.security.properties.CodingApiSecurityProperties;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * DefaultHttpSecurityCustomer 单元测试
 * <p>
 * 通过 mock HttpSecurity 覆盖 customize 中各开关的 true/false 分支，
 * 并手动触发 cors/csrf/headers 的 Customizer lambda 以覆盖其内部分支。
 */
class DefaultHttpSecurityCustomerTest {

    @Test
    void customizeWithAllDisableFlagsTrue() throws Exception {
        HttpSecurity security = mock(HttpSecurity.class, Mockito.RETURNS_DEEP_STUBS);
        CodingApiSecurityProperties properties = new CodingApiSecurityProperties();

        new DefaultHttpSecurityCustomer(properties).customize(security);

        verify(security).httpBasic(any());
        verify(security).headers(any());
        verify(security).cors(any());
        verify(security).csrf(any());
    }

    @Test
    void customizeWithBasicAuthAndFrameOptionsEnabled() throws Exception {
        HttpSecurity security = mock(HttpSecurity.class, Mockito.RETURNS_DEEP_STUBS);
        CodingApiSecurityProperties properties = new CodingApiSecurityProperties();
        properties.setDisableBasicAuth(false);
        properties.setDisableFrameOptions(false);

        new DefaultHttpSecurityCustomer(properties).customize(security);

        verify(security, never()).httpBasic(any());
        verify(security, never()).headers(any());
        // cors/csrf 的 Customizer 始终会被调用，内部再根据开关决定是否 disable
        verify(security).cors(any());
        verify(security).csrf(any());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    void corsAndCsrfCustomizerDisableWhenFlagsTrue() throws Exception {
        HttpSecurity security = mock(HttpSecurity.class, Mockito.RETURNS_DEEP_STUBS);
        CodingApiSecurityProperties properties = new CodingApiSecurityProperties();
        new DefaultHttpSecurityCustomer(properties).customize(security);

        ArgumentCaptor<Customizer> corsCaptor = ArgumentCaptor.forClass(Customizer.class);
        verify(security).cors(corsCaptor.capture());
        CorsConfigurer corsConfigurer = mock(CorsConfigurer.class);
        corsCaptor.getValue().customize(corsConfigurer);
        verify(corsConfigurer).disable();

        ArgumentCaptor<Customizer> csrfCaptor = ArgumentCaptor.forClass(Customizer.class);
        verify(security).csrf(csrfCaptor.capture());
        CsrfConfigurer csrfConfigurer = mock(CsrfConfigurer.class);
        csrfCaptor.getValue().customize(csrfConfigurer);
        verify(csrfConfigurer).disable();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    void corsAndCsrfCustomizerKeepEnabledWhenFlagsFalse() throws Exception {
        HttpSecurity security = mock(HttpSecurity.class, Mockito.RETURNS_DEEP_STUBS);
        CodingApiSecurityProperties properties = new CodingApiSecurityProperties();
        properties.setDisableCors(false);
        properties.setDisableCsrf(false);
        new DefaultHttpSecurityCustomer(properties).customize(security);

        ArgumentCaptor<Customizer> corsCaptor = ArgumentCaptor.forClass(Customizer.class);
        verify(security).cors(corsCaptor.capture());
        CorsConfigurer corsConfigurer = mock(CorsConfigurer.class);
        corsCaptor.getValue().customize(corsConfigurer);
        verify(corsConfigurer, never()).disable();

        ArgumentCaptor<Customizer> csrfCaptor = ArgumentCaptor.forClass(Customizer.class);
        verify(security).csrf(csrfCaptor.capture());
        CsrfConfigurer csrfConfigurer = mock(CsrfConfigurer.class);
        csrfCaptor.getValue().customize(csrfConfigurer);
        verify(csrfConfigurer, never()).disable();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    void headersCustomizerDisablesFrameOptions() throws Exception {
        HttpSecurity security = mock(HttpSecurity.class, Mockito.RETURNS_DEEP_STUBS);
        new DefaultHttpSecurityCustomer(new CodingApiSecurityProperties()).customize(security);

        ArgumentCaptor<Customizer> headersCaptor = ArgumentCaptor.forClass(Customizer.class);
        verify(security).headers(headersCaptor.capture());
        HeadersConfigurer headersConfigurer = mock(HeadersConfigurer.class);
        headersCaptor.getValue().customize(headersConfigurer);

        ArgumentCaptor<Customizer> frameOptionsCaptor = ArgumentCaptor.forClass(Customizer.class);
        verify(headersConfigurer).frameOptions(frameOptionsCaptor.capture());
        HeadersConfigurer.FrameOptionsConfig frameOptionsConfig = mock(HeadersConfigurer.FrameOptionsConfig.class);
        frameOptionsCaptor.getValue().customize(frameOptionsConfig);
        verify(frameOptionsConfig).disable();
    }

}

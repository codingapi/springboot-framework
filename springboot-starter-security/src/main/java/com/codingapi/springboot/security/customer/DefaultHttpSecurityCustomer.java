package com.codingapi.springboot.security.customer;

import com.codingapi.springboot.security.properties.CodingApiSecurityProperties;
import lombok.AllArgsConstructor;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;

@AllArgsConstructor
public class DefaultHttpSecurityCustomer implements HttpSecurityCustomer {

    private final CodingApiSecurityProperties properties;

    @Override
    public void customize(HttpSecurity security) throws Exception {

        //disable basic auth
        if (properties.isDisableBasicAuth()) {
            security.httpBasic(AbstractHttpConfigurer::disable);
        }

        //disable frame options
        if (properties.isDisableFrameOptions()) {
            security.headers(new Customizer<HeadersConfigurer<HttpSecurity>>() {
                @Override
                public void customize(HeadersConfigurer<HttpSecurity> httpSecurityHeadersConfigurer) {
                    httpSecurityHeadersConfigurer.frameOptions(new Customizer<HeadersConfigurer<org.springframework.security.config.annotation.web.builders.HttpSecurity>.FrameOptionsConfig>() {
                        @Override
                        public void customize(HeadersConfigurer<HttpSecurity>.FrameOptionsConfig frameOptionsConfig) {
                            frameOptionsConfig.disable();
                        }
                    });
                }
            });
        }

        //before add addCorsMappings to enable cors.
        security.cors(httpSecurityCorsConfigurer -> {
            if (properties.isDisableCors()) {
                httpSecurityCorsConfigurer.disable();
            }
        });

        security.csrf(httpSecurityCsrfConfigurer -> {
            if (properties.isDisableCsrf()) {
                httpSecurityCsrfConfigurer.disable();
            }
        });
    }
}

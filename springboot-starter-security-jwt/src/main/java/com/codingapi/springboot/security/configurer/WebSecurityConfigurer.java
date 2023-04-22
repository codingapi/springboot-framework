package com.codingapi.springboot.security.configurer;

import com.codingapi.springboot.security.properties.SecurityJwtProperties;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

@Configuration
@AllArgsConstructor
public class WebSecurityConfigurer implements WebSecurityCustomizer {

    private final SecurityJwtProperties securityJwtProperties;

    @Override
    public void customize(WebSecurity web) {
        //ignoring security filters request url
        web.ignoring().requestMatchers(securityJwtProperties.getIgnoreUrls());
    }

}

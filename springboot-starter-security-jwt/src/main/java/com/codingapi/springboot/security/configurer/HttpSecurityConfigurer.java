package com.codingapi.springboot.security.configurer;

import com.codingapi.springboot.security.filter.MyAuthenticationFilter;
import com.codingapi.springboot.security.filter.MyLoginFilter;
import com.codingapi.springboot.security.filter.SecurityLoginHandler;
import com.codingapi.springboot.security.jwt.Jwt;
import com.codingapi.springboot.security.properties.SecurityJwtProperties;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@AllArgsConstructor
public class HttpSecurityConfigurer extends AbstractHttpConfigurer<HttpSecurityConfigurer, HttpSecurity> {

    private final Jwt jwt;

    private final SecurityLoginHandler securityLoginHandler;
    private final SecurityJwtProperties securityJwtProperties;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        AuthenticationManager manager = http.getSharedObject(AuthenticationManager.class);
        http.addFilter(new MyLoginFilter(manager, jwt,securityLoginHandler, securityJwtProperties));
        http.addFilter(new MyAuthenticationFilter(manager, jwt));
    }
}

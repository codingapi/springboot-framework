package com.codingapi.springboot.security.jwt;

import com.codingapi.springboot.security.gateway.TokenGateway;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "codingapi.security.jwt", name = "enable", havingValue = "true")
public class JWTSecurityConfiguration {


    @Bean
    @ConfigurationProperties(prefix = "codingapi.security.jwt")
    public SecurityJWTProperties securityJWTProperties() {
        return new SecurityJWTProperties();
    }


    @Bean
    @ConditionalOnMissingBean
    public JwtTokenGateway jwtTokenGateway(SecurityJWTProperties properties) {
        return new JwtTokenGateway(properties);
    }


    @Bean
    @ConditionalOnMissingBean
    public TokenGateway jwtTokenGatewayImpl(JwtTokenGateway jwtTokenGateway) {
        return new JWTTokenGatewayImpl(jwtTokenGateway);
    }

}

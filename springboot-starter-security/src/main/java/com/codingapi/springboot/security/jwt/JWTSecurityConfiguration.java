package com.codingapi.springboot.security.jwt;

import com.codingapi.springboot.framework.crypto.AES;
import com.codingapi.springboot.security.gateway.TokenGateway;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Base64;

@Configuration
@ConditionalOnProperty(prefix = "codingapi.security.jwt", name = "enable", havingValue = "true", matchIfMissing = true)
public class JWTSecurityConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AES aes(SecurityJWTProperties properties) throws Exception {
        AES aes = new AES(Base64.getDecoder().decode(properties.getAseKey().getBytes()),
                Base64.getDecoder().decode(properties.getAseIv()));
        MyAES.getInstance().init(aes);
        return aes;
    }

    @Bean
    @ConfigurationProperties(prefix = "codingapi.security.jwt")
    public SecurityJWTProperties securityJWTProperties() {
        return new SecurityJWTProperties();
    }


    @Bean
    @ConditionalOnMissingBean
    public TokenGateway jwtTokenGateway(SecurityJWTProperties properties) {
        return new JWTTokenGatewayImpl(properties);
    }

}

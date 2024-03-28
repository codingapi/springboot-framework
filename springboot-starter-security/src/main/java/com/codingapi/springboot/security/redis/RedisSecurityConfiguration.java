package com.codingapi.springboot.security.redis;

import com.codingapi.springboot.security.gateway.TokenGateway;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
@ConditionalOnProperty(prefix = "codingapi.security.redis", name = "enable", havingValue = "true")
public class RedisSecurityConfiguration {


    @Bean
    @ConfigurationProperties(prefix = "codingapi.security.redis")
    public SecurityRedisProperties securityRedisProperties() {
        return new SecurityRedisProperties();
    }


    @Bean
    @ConditionalOnMissingBean
    public RedisTokenGateway redisTokenGateway(RedisTemplate<String, String> redisTemplate, SecurityRedisProperties properties) {
        return new RedisTokenGateway(redisTemplate, properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public TokenGateway redisTokenGatewayImpl(RedisTokenGateway redisTokenGateway) {
        return new RedisTokenGatewayImpl(redisTokenGateway);
    }

}

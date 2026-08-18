package com.codingapi.springboot.security.redis;

import com.codingapi.springboot.security.gateway.TokenGateway;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.RedisTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * RedisSecurityConfiguration 单元测试
 * <p>
 * 直接调用配置类的 Bean 工厂方法（RedisTemplate 使用 mock），
 * 验证各 Bean 的创建逻辑。
 */
class RedisSecurityConfigurationTest {

    @SuppressWarnings("unchecked")
    @Test
    void beanFactoryMethodsCreateExpectedBeans() {
        RedisSecurityConfiguration configuration = new RedisSecurityConfiguration();

        SecurityRedisProperties properties = configuration.securityRedisProperties();
        assertNotNull(properties);
        assertTrue(properties.isEnable());

        RedisTemplate<String, String> redisTemplate = mock(RedisTemplate.class);
        RedisTokenGateway redisTokenGateway = configuration.redisTokenGateway(redisTemplate, properties);
        assertNotNull(redisTokenGateway);

        TokenGateway tokenGateway = configuration.redisTokenGatewayImpl(redisTokenGateway);
        assertNotNull(tokenGateway);
        assertTrue(tokenGateway instanceof RedisTokenGatewayImpl);
    }

}

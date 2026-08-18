package com.codingapi.springboot.security.jwt;

import com.codingapi.springboot.security.gateway.TokenGateway;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JWTSecurityConfiguration 单元测试
 * <p>
 * 直接调用配置类的 Bean 工厂方法，验证各 Bean 的创建逻辑。
 */
class JWTSecurityConfigurationTest {

    @Test
    void beanFactoryMethodsCreateExpectedBeans() {
        JWTSecurityConfiguration configuration = new JWTSecurityConfiguration();

        SecurityJWTProperties properties = configuration.securityJWTProperties();
        assertNotNull(properties);
        assertTrue(properties.isEnable());

        JwtTokenGateway jwtTokenGateway = configuration.jwtTokenGateway(properties);
        assertNotNull(jwtTokenGateway);

        TokenGateway tokenGateway = configuration.jwtTokenGatewayImpl(jwtTokenGateway);
        assertNotNull(tokenGateway);
        assertTrue(tokenGateway instanceof JWTTokenGatewayImpl);
    }

}

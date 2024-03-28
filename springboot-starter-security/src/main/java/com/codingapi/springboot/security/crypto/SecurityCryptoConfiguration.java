package com.codingapi.springboot.security.crypto;

import com.codingapi.springboot.framework.crypto.AES;
import com.codingapi.springboot.security.properties.CodingApiSecurityProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Base64;

@Configuration
public class SecurityCryptoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AES aes(CodingApiSecurityProperties properties) throws Exception {
        AES aes = new AES(Base64.getDecoder().decode(properties.getAseKey().getBytes()),
                Base64.getDecoder().decode(properties.getAseIv()));
        AESTools.getInstance().init(aes);
        return aes;
    }
}

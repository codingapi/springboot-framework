package com.codingapi.springboot.security.crypto;

import com.codingapi.springboot.framework.crypto.AES;
import com.codingapi.springboot.security.properties.SecurityJwtProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Base64Utils;

@Configuration
public class MyCryptoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AES aes(SecurityJwtProperties properties) throws Exception {
        AES aes = new AES(Base64Utils.decodeFromString(properties.getAseKey()), Base64Utils.decodeFromString(properties.getAseIv()));
        MyAES.getInstance().init(aes);
        return aes;
    }

}

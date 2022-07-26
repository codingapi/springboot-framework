package com.codingapi.springboot.leaf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lorne
 * @since 1.0.0
 */
@Configuration
public class AutoConfiguration {

    @Bean(initMethod = "init")
    public LeafClient leafClient(){
        return new LeafClient();
    }
}

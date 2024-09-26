package com.codingapi.example.repository;

import com.codingapi.example.domain.Node;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RepositoryRegisterConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public NodeRepository nodeRepository() {
        log.info("default nodeRepository register.");
        return new NodeRepository() {
            @Override
            public void save(Node node) {
            }

            @Override
            public void delete(int id) {
            }
        };
    }
}

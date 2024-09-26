package com.codingapi.example.infrastructure.jpa.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@EntityScan(basePackages = "com.codingapi.example.domain")
@EnableJpaRepositories(basePackages = "com.codingapi.example.infrastructure.jpa")
@Configuration
public class ExampleJpaConfiguration {


}

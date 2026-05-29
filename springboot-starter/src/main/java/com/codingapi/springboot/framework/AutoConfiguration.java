package com.codingapi.springboot.framework;

import com.codingapi.springboot.framework.properties.FrameworkProperties;
import com.codingapi.springboot.framework.properties.PropertiesContext;
import com.codingapi.springboot.framework.utils.VersionUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AutoConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        String version = VersionUtils.getDriverVersion();
        this.printBanner(version);
    }

    @Bean
    @ConfigurationProperties(prefix = "codingapi.framework")
    public FrameworkProperties frameworkProperties() {
        FrameworkProperties properties = new FrameworkProperties();
        PropertiesContext.getInstance().setProperties(properties);
        return properties;
    }
    public void printBanner(String version) {
        System.out.println("------------------------------------------------------");
        System.out.println("\t\tCodingApi SpringBoot-Starter " + version);
        System.out.println("------------------------------------------------------");
    }
}

package com.codingapi.springboot.framework;

import com.codingapi.springboot.framework.utils.VersionUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AutoConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        String version = VersionUtils.getDriverVersion();
        this.printBanner(version);
    }

    public void printBanner(String version) {
        System.out.println("------------------------------------------------------");
        System.out.println("\t\tCodingApi SpringBoot-Starter " + version);
        System.out.println("------------------------------------------------------");
    }
}

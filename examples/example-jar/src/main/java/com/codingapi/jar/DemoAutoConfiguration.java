package com.codingapi.jar;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.codingapi.jar.repository",
        entityManagerFactoryRef = "hiEntityManagerFactory",
        transactionManagerRef = "hiTransactionManager"
)
public class DemoAutoConfiguration {


    @Bean(name = "hiEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean hiEntityManagerFactory() {
        DataSource dataSource = DataSourceBuilder.create()
                .url("jdbc:h2:file:./hi.db")
                .driverClassName("org.h2.Driver")
                .build();

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.codingapi.jar.entity");

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        return em;
    }

    @Bean(name = "hiTransactionManager")
    public PlatformTransactionManager hiTransactionManager(
            @Qualifier(value = "hiEntityManagerFactory") LocalContainerEntityManagerFactoryBean hiEntityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(hiEntityManagerFactory.getObject());
        return transactionManager;
    }


}
package com.codingapi.springboot.framework.transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class TransactionManagerContextConfiguration {

    @Bean
    public TransactionManagerContextRegister transactionManagerContextRegister(@Autowired(required = false) PlatformTransactionManager platformTransactionManager){
        return new TransactionManagerContextRegister(platformTransactionManager);
    }

}

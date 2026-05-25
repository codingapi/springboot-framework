package com.codingapi.springboot.framework.transaction;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class TransactionManagerContextConfiguration {

    @Bean
    public TransactionManagerContextRegister transactionManagerContextRegister(PlatformTransactionManager platformTransactionManager){
        return new TransactionManagerContextRegister(platformTransactionManager);
    }

}

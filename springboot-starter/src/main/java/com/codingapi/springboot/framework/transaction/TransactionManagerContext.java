package com.codingapi.springboot.framework.transaction;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
public class TransactionManagerContext {

    @Getter
    private PlatformTransactionManager platformTransactionManager;

    @Getter
    private final static TransactionManagerContext instance = new TransactionManagerContext();

    private TransactionManagerContext() {}

    public void setPlatformTransactionManager(PlatformTransactionManager platformTransactionManager) {
        this.platformTransactionManager = platformTransactionManager;
        log.info("platformTransactionManager:{} load success",platformTransactionManager);
    }
}

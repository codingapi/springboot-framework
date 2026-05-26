package com.codingapi.springboot.framework.transaction;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.PlatformTransactionManager;

public class TransactionManagerContextRegister implements InitializingBean {

    private final PlatformTransactionManager transactionManager;

    public TransactionManagerContextRegister(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        TransactionManagerContext.getInstance().setPlatformTransactionManager(transactionManager);
    }


}

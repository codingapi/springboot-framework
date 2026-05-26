package com.codingapi.springboot.framework.transaction;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.function.Supplier;

@Slf4j
public class TransactionManagerContext {

    @Getter
    private PlatformTransactionManager platformTransactionManager;

    @Getter
    private final static TransactionManagerContext instance = new TransactionManagerContext();

    private TransactionManagerContext() {
    }

    public void setPlatformTransactionManager(PlatformTransactionManager platformTransactionManager) {
        this.platformTransactionManager = platformTransactionManager;
        if (platformTransactionManager != null) {
            log.info("platformTransactionManager:{} load success", platformTransactionManager);
        }
    }


    public <T> T commit(Supplier<T> supplier) {
        if (platformTransactionManager != null) {
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            TransactionStatus transactionStatus = platformTransactionManager.getTransaction(def);
            try {
                T result = supplier.get();
                platformTransactionManager.commit(transactionStatus);
                return result;
            } catch (Exception e) {
                platformTransactionManager.rollback(transactionStatus);
                throw e;
            }
        }
        return supplier.get();
    }

    public <T> T readOnly(Supplier<T> supplier) {
        if (platformTransactionManager != null) {
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            def.setReadOnly(true);
            TransactionStatus transactionStatus = platformTransactionManager.getTransaction(def);
            try {
                T result = supplier.get();
                platformTransactionManager.rollback(transactionStatus);
                return result;
            } catch (Exception e) {
                platformTransactionManager.rollback(transactionStatus);
                throw e;
            }
        }
        return supplier.get();
    }
}

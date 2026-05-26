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
        log.info("platformTransactionManager:{} load success", platformTransactionManager);
    }


    public <T> T commit(Supplier<T> supplier) {
        PlatformTransactionManager transactionManager = TransactionManagerContext.getInstance().getPlatformTransactionManager();
        if (transactionManager != null) {
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            TransactionStatus transactionStatus = transactionManager.getTransaction(def);
            try {
                T result = supplier.get();
                transactionManager.commit(transactionStatus);
                return result;
            } catch (Exception e) {
                transactionManager.rollback(transactionStatus);
                throw e;
            }
        }
        return supplier.get();
    }

    public <T> T readOnly(Supplier<T> supplier){
        PlatformTransactionManager transactionManager = TransactionManagerContext.getInstance().getPlatformTransactionManager();
        if (transactionManager != null) {
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            def.setReadOnly(true);
            TransactionStatus transactionStatus = transactionManager.getTransaction(def);
            try {
                T result = supplier.get();
                transactionManager.rollback(transactionStatus);
                return result;
            } catch (Exception e) {
                transactionManager.rollback(transactionStatus);
                throw e;
            }
        }
        return supplier.get();
    }
}

package com.codingapi.springboot.framework.transaction;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * TransactionManagerContext 单元测试
 */
class TransactionManagerContextTest {

    private PlatformTransactionManager transactionManager;
    private TransactionStatus transactionStatus;

    @BeforeEach
    void setUp() {
        transactionManager = mock(PlatformTransactionManager.class);
        transactionStatus = mock(TransactionStatus.class);
        when(transactionManager.getTransaction(any(TransactionDefinition.class))).thenReturn(transactionStatus);
    }

    @AfterEach
    void tearDown() {
        // 恢复为空, 避免影响其他测试
        TransactionManagerContext.getInstance().setPlatformTransactionManager(null);
    }

    @Test
    void commitWithTransactionManager() {
        TransactionManagerContext.getInstance().setPlatformTransactionManager(transactionManager);

        String result = TransactionManagerContext.getInstance().commit(() -> "ok");

        assertEquals("ok", result);
        ArgumentCaptor<TransactionDefinition> captor = ArgumentCaptor.forClass(TransactionDefinition.class);
        verify(transactionManager).getTransaction(captor.capture());
        assertEquals(TransactionDefinition.PROPAGATION_REQUIRES_NEW, captor.getValue().getPropagationBehavior());
        verify(transactionManager).commit(transactionStatus);
        verify(transactionManager, never()).rollback(transactionStatus);
    }

    @Test
    void commitRollsBackOnException() {
        TransactionManagerContext.getInstance().setPlatformTransactionManager(transactionManager);

        assertThrows(IllegalStateException.class,
                () -> TransactionManagerContext.getInstance().commit(() -> {
                    throw new IllegalStateException("boom");
                }));

        verify(transactionManager).rollback(transactionStatus);
        verify(transactionManager, never()).commit(transactionStatus);
    }

    @Test
    void commitWithoutTransactionManager() {
        TransactionManagerContext.getInstance().setPlatformTransactionManager(null);

        String result = TransactionManagerContext.getInstance().commit(() -> "direct");

        assertEquals("direct", result);
    }

    @Test
    void readOnlyEndsWithRollback() {
        TransactionManagerContext.getInstance().setPlatformTransactionManager(transactionManager);

        String result = TransactionManagerContext.getInstance().readOnly(() -> "read");

        assertEquals("read", result);
        ArgumentCaptor<TransactionDefinition> captor = ArgumentCaptor.forClass(TransactionDefinition.class);
        verify(transactionManager).getTransaction(captor.capture());
        DefaultTransactionDefinition definition = (DefaultTransactionDefinition) captor.getValue();
        assertEquals(TransactionDefinition.PROPAGATION_REQUIRES_NEW, definition.getPropagationBehavior());
        assertEquals(true, definition.isReadOnly());
        // 只读模式以回滚结束
        verify(transactionManager).rollback(transactionStatus);
        verify(transactionManager, never()).commit(transactionStatus);
    }

    @Test
    void readOnlyRollsBackOnException() {
        TransactionManagerContext.getInstance().setPlatformTransactionManager(transactionManager);

        assertThrows(IllegalStateException.class,
                () -> TransactionManagerContext.getInstance().readOnly(() -> {
                    throw new IllegalStateException("boom");
                }));

        verify(transactionManager).rollback(transactionStatus);
    }

    @Test
    void readOnlyWithoutTransactionManager() {
        TransactionManagerContext.getInstance().setPlatformTransactionManager(null);

        String result = TransactionManagerContext.getInstance().readOnly(() -> "direct");

        assertEquals("direct", result);
    }
}

package com.codingapi.springboot.framework.event;

import java.util.List;

/**
 * 事务事件处理器的兼容别名。
 *
 * @deprecated 自事件分发统一为「事务提交后按 trace 串行」后，本类与
 * {@link SpringDefaultEventHandler} 行为完全一致。保留仅用于兼容按类名注入的既有代码；
 * 新代码请直接使用 {@link SpringDefaultEventHandler}。配置
 * {@code codingapi.framework.event.transaction.enable} 不再产生行为差异。
 */
@Deprecated
public class SpringTransactionEventHandler extends SpringDefaultEventHandler {

    public SpringTransactionEventHandler(List<IHandler> handlers) {
        super(handlers);
    }
}

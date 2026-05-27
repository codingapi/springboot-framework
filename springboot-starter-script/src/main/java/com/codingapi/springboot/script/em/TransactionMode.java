package com.codingapi.springboot.script.em;

public enum TransactionMode {
    // 默认不处理
    DEFAULT,
    // 事务提交模式
    COMMIT,
    // 事务只读模式
    READONLY
}

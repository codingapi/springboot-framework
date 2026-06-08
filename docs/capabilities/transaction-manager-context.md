---
name: transaction-manager-context
description: 单例编程式事务上下文，支持 REQUIRES_NEW 语义，在非 Spring 管理上下文中也能使用事务
status: 已实现
scope: 后端
source: 项目自有
last_commit: 398bf849
code_files:
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/transaction/TransactionManagerContext.java
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/transaction/TransactionManagerContextConfiguration.java
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/transaction/TransactionManagerContextRegister.java
---

## 解决什么问题

在以下场景中，标准的 Spring `@Transactional` 声明式事务无法满足需求：

1. **非 Spring 管理的代码**：工具类、静态方法、回调函数中无法使用 `@Transactional` 注解
2. **需要独立新事务**：当前已处于一个事务中，但某段逻辑需要在独立的新事务中执行（`REQUIRES_NEW`），且不受外层事务回滚影响
3. **动态事务控制**：需要根据运行时条件决定是否开启事务、使用只读事务还是读写事务
4. **框架内部基础设施**：事件处理器、脚本引擎等框架组件需要在自身逻辑中管理事务边界

本能力提供了一个全局单例的编程式事务上下文，封装了 Spring `PlatformTransactionManager`，使得在任何位置都能以简洁的 Lambda 方式执行事务操作。核心特点：

- **全局单例访问**：通过 `TransactionManagerContext.getInstance()` 在任何位置获取
- **REQUIRES_NEW 语义**：每次调用都开启独立新事务，不与调用方共享事务
- **自动初始化**：Spring 容器启动时自动注入 `PlatformTransactionManager`
- **优雅降级**：未配置事务管理器时直接执行业务逻辑，不抛异常

## 如何使用

### 核心 API

#### TransactionManagerContext — 事务上下文（单例）

```java
// 获取单例实例
TransactionManagerContext ctx = TransactionManagerContext.getInstance();

// 在读写事务中执行（REQUIRES_NEW）
<T> T commit(Supplier<T> supplier)

// 在只读事务中执行（REQUIRES_NEW + readOnly=true）
<T> T readOnly(Supplier<T> supplier)
```

### 自动配置

框架通过 `TransactionManagerContextConfiguration` 自动完成初始化：

1. `TransactionManagerContextConfiguration` 注册 `TransactionManagerContextRegister` Bean
2. `TransactionManagerContextRegister` 实现 `InitializingBean`，在 Bean 属性设置完成后将 Spring 容器中的 `PlatformTransactionManager` 注入到 `TransactionManagerContext` 单例中
3. 如果容器中不存在 `PlatformTransactionManager`（`@Autowired(required = false)`），则不注入，事务操作会降级为直接执行

无需任何额外配置，只要项目中存在数据源和事务管理器即可自动生效。

### 事务行为说明

| 方法 | 传播行为 | 只读 | 正常结束 | 异常时 |
|------|---------|------|---------|--------|
| `commit()` | REQUIRES_NEW | 否 | 提交事务 | 回滚事务并抛出异常 |
| `readOnly()` | REQUIRES_NEW | 是 | 回滚事务（只读无需提交） | 回滚事务并抛出异常 |

> **注意**：`readOnly()` 方法在正常结束时也会调用 `rollback()` 而非 `commit()`，因为只读事务没有数据变更，回滚比提交更高效。

## 使用实例

### 1. 在工具类中使用事务

```java
public class DataMigrationUtil {

    public static void migrateData(List<Record> records) {
        TransactionManagerContext.getInstance().commit(() -> {
            // 这些操作在独立的新事务中执行
            for (Record record : records) {
                recordRepository.save(record);
            }
            return null;
        });
    }
}
```

### 2. 在事件处理器中使用独立事务

```java
@Component
public class OrderCreatedHandler implements IHandler<OrderCreatedEvent> {

    @Override
    public void handler(OrderCreatedEvent event) {
        // 即使外层事务回滚，库存扣减也在独立事务中完成
        TransactionManagerContext.getInstance().commit(() -> {
            inventoryService.deduct(event.getProductId(), event.getQuantity());
            return null;
        });
    }
}
```

### 3. 使用只读事务查询

```java
public List<Report> generateReport() {
    return TransactionManagerContext.getInstance().readOnly(() -> {
        // 只读事务，数据库可优化查询性能
        List<Order> orders = orderRepository.findByMonth(currentMonth);
        List<Product> products = productRepository.findAll();
        return reportBuilder.build(orders, products);
    });
}
```

### 4. 无事务管理器时的降级行为

```java
// 当 PlatformTransactionManager 未注入时，supplier 直接执行，不包裹事务
// 适用于测试环境或无数据库的场景
String result = TransactionManagerContext.getInstance().commit(() -> {
    return "direct execution without transaction";
});
```

### 5. 异常自动回滚

```java
try {
    TransactionManagerContext.getInstance().commit(() -> {
        accountService.debit(fromAccount, amount);
        accountService.credit(toAccount, amount);
        if (balanceCheckFailed) {
            throw new IllegalStateException("余额校验失败");
        }
        return null;
    });
} catch (IllegalStateException e) {
    // 事务已自动回滚，debit 和 credit 操作均已撤销
    log.error("转账失败，事务已回滚", e);
}
```

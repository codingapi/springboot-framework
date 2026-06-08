---
name: slf4j-logging
description: 所有 Java 类必须使用 Lombok @Slf4j 注解进行日志输出，禁止直接使用 LoggerFactory.getLogger()
status: 已实现
scope: 全栈
source: 项目自有
last_commit: N/A
code_files: []
---

## 解决什么问题

如果每个类都手动创建 Logger 实例，会导致以下问题：

1. **样板代码冗余**：每个类都需要声明 `private static final Logger log = LoggerFactory.getLogger(XxxClass.class)`，增加无意义的代码量
2. **类名引用易出错**：复制粘贴时忘记修改 `getLogger()` 中的类名参数，导致日志输出错误的来源标识
3. **重构风险**：重命名类后若未同步修改 Logger 声明，日志追踪将指向旧类名
4. **风格不一致**：部分开发者使用 `Logger`、部分使用 `Log`、部分使用 `logger` 等不同的变量命名

Lombok 的 `@Slf4j` 注解在编译期自动生成 `private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(当前类.class);`，消除上述所有问题。

## 如何使用

### 规则

1. **所有需要日志输出的 Java 类必须在类级别添加 `@Slf4j` 注解**
2. 使用生成的 `log` 变量进行日志输出（`log.info()`、`log.warn()`、`log.error()` 等）
3. **禁止**在代码中出现 `LoggerFactory.getLogger()` 调用
4. **禁止**手动声明 `Logger` / `Log` 字段
5. 日志消息使用 SLF4J 占位符 `{}` 格式，不使用字符串拼接
6. 异常日志应将异常对象作为最后一个参数传入，而非调用 `e.getMessage()`

### 注意事项

- `@Slf4j` 是 Lombok 注解，需确保项目中已引入 Lombok 依赖且 IDE 已安装 Lombok 插件
- 对于内部静态类，Lombok 会自动生成对应内部类的 Logger，无需额外处理
- 如需自定义 Logger 名称（极少数场景），可使用 `@Slf4j(topic = "custom.topic")`

## 使用实例

### ✅ 正确示例

```java
@Slf4j
@Service
public class UserService {

    public UserEntity getById(Long id) {
        log.info("查询用户, id={}", id);
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new LocaleMessageException("user.not.found"));
        log.debug("用户查询成功, id={}, name={}", id, user.getName());
        return user;
    }

    public void deleteUser(Long id) {
        try {
            userRepository.deleteById(id);
            log.info("用户删除成功, id={}", id);
        } catch (Exception e) {
            // 异常对象作为最后一个参数，SLF4J 会自动打印堆栈
            log.error("用户删除失败, id={}", id, e);
            throw e;
        }
    }
}

// 内部静态类也可正常使用
@Configuration
public class BasicHandlerExceptionResolverConfiguration {

    @Slf4j
    public static class ServletExceptionHandler implements HandlerExceptionResolver {
        @Override
        public ModelAndView resolveException(...) {
            log.warn("controller exception:{}", ex.getLocalizedMessage(), ex);
            // ...
        }
    }
}
```

### ❌ 错误示例

```java
// 错误：手动创建 Logger，存在样板代码和类名引用风险
@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserEntity getById(Long id) {
        logger.info("查询用户, id=" + id);  // 还使用了字符串拼接
        // ...
    }
}

// 错误：使用不同变量名，风格不统一
@Service
public class OrderService {
    private static final Log LOG = LogFactory.getLog(OrderService.class);
    // ...
}

// 错误：异常日志未传入异常对象
try {
    doSomething();
} catch (Exception e) {
    log.error("操作失败: " + e.getMessage());  // 丢失堆栈信息
}

// 错误：缺少 @Slf4j 注解却直接使用 log 变量（编译报错）
@Service
public class PaymentService {
    public void pay() {
        log.info("开始支付");  // 编译错误：找不到符号 log
    }
}
```

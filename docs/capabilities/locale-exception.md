---
name: locale-exception
description: 支持 i18n 的业务异常体系，errCode + errMessage，通过 MessageSource 实现国际化
status: 已实现
scope: 后端
source: 项目自有
last_commit: 0c4299a1
code_files:
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/exception/LocaleMessageException.java
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/exception/LocaleMessage.java
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/exception/MessageContext.java
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/exception/ExceptionConfiguration.java
---

## 解决什么问题

在企业级应用中，业务异常需要同时满足以下需求：

1. **结构化错误信息**：异常需携带机器可读的错误码（errCode）和人类可读的错误消息（errMessage），便于前端根据 errCode 做差异化处理
2. **国际化支持**：同一错误码在不同语言环境下返回对应语言的错误消息，而非硬编码中文字符串
3. **参数化消息**：错误消息支持占位符（如 `{0}`、`{1}`），运行时动态填充具体值
4. **与统一响应对接**：异常的 errCode/errMessage 可直接映射到框架的 `Response` DTO，由全局异常处理器统一返回

本能力基于 Spring `MessageSource` 构建了国际化异常体系，开发者只需在 `messages.properties` 中定义错误码与消息的映射关系，代码中通过错误码抛出异常即可自动解析当前 Locale 对应的消息。

## 如何使用

### 核心组件

#### LocaleMessageException — 国际化业务异常

```java
public class LocaleMessageException extends RuntimeException {
    String getErrCode()      // 错误码
    String getErrMessage()   // 解析后的错误消息（当前 Locale）
}
```

提供多种构造方式：

| 构造函数 | 说明 |
|---------|------|
| `LocaleMessageException(errCode)` | 仅错误码，从 MessageSource 解析消息 |
| `LocaleMessageException(errCode, args)` | 错误码 + 占位符参数 |
| `LocaleMessageException(errCode, errMessage)` | 错误码 + 自定义消息（不走 i18n） |
| `LocaleMessageException(errCode, args, cause)` | 错误码 + 参数 + 原始异常 |
| `LocaleMessageException.of(errCode, args...)` | 静态工厂方法（推荐） |

#### MessageContext — 消息上下文（单例）

内部持有 `LocaleMessage` 实例，提供 `getErrorMsg(errCode)` 和 `getErrorMsg(errCode, args)` 方法。`LocaleMessageException` 的无消息构造函数通过此上下文获取国际化消息。

#### LocaleMessage — 消息解析器

封装 Spring `MessageSource`，根据 `LocaleContextHolder.getLocale()` 自动获取当前请求的语言环境并解析消息。

### 自动配置

`ExceptionConfiguration` 自动注册 `LocaleMessage` Bean：

```java
@Configuration
public class ExceptionConfiguration {
    @Bean(initMethod = "init")
    public LocaleMessage exceptionLocaleMessage(MessageSource messageSource) {
        return new LocaleMessage(messageSource);
    }
}
```

`init()` 方法将 `LocaleMessage` 注入到 `MessageContext` 单例中。只要 Spring 容器中存在 `MessageSource`（通常由 `messages.properties` 自动配置），即可直接使用。

### 消息资源文件

在 `src/main/resources/` 下创建消息资源文件：

```properties
# messages.properties (默认/中文)
user.not.found=用户不存在
order.amount.exceeded=订单金额超出限制，最大金额为 {0} 元
account.locked=账户 {0} 已被锁定，请联系管理员

# messages_en.properties (英文)
user.not.found=User not found
order.amount.exceeded=Order amount exceeded, maximum is {0} yuan
account.locked=Account {0} has been locked, please contact administrator
```

## 使用实例

### 1. 基本用法 — 仅错误码

```java
// 从 messages.properties 中解析 "user.not.found" 对应的消息
throw new LocaleMessageException("user.not.found");
// 中文环境 → errMessage: "用户不存在"
// 英文环境 → errMessage: "User not found"
```

### 2. 带参数的消息

```java
// 使用静态工厂方法（推荐）
throw LocaleMessageException.of("order.amount.exceeded", 10000);
// → errMessage: "订单金额超出限制，最大金额为 10000 元"

// 或使用构造函数
throw new LocaleMessageException("account.locked", new Object[]{"admin@example.com"});
// → errMessage: "账户 admin@example.com 已被锁定，请联系管理员"
```

### 3. 携带原始异常

```java
try {
    userRepository.findById(userId);
} catch (DataAccessException e) {
    throw new LocaleMessageException("user.not.found", e);
}
```

### 4. 自定义消息（跳过 i18n）

```java
// 直接指定消息文本，不从 MessageSource 解析
throw new LocaleMessageException("custom.error", "这是一条自定义错误消息");
```

### 5. 与全局异常处理器配合

在全局异常处理器中捕获 `LocaleMessageException`，提取 errCode 和 errMessage 构建统一响应：

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LocaleMessageException.class)
    public Response handleLocaleException(LocaleMessageException e) {
        return Response.buildFailure(e.getErrCode(), e.getErrMessage());
    }
}
```

返回的 JSON 响应：

```json
{
    "success": false,
    "errCode": "order.amount.exceeded",
    "errMessage": "订单金额超出限制，最大金额为 10000 元"
}
```

### 6. Locale 切换

框架通过 `LocaleContextHolder` 获取当前 Locale，与 Spring MVC 的 `Accept-Language` 请求头或自定义 LocaleResolver 集成。无需额外配置，客户端发送不同的 `Accept-Language` 头即可获得对应语言的错误消息。

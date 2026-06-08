---
name: springboot-starter/locale-message
module: springboot-starter
description: 国际化异常消息，支持多语言异常信息和本地化消息解析
status: 已实现
scope: 后端
source: 框架:springboot-starter
import: "com.codingapi.springboot:springboot-starter"
framework_version: "3.4.54"
---

## 解决什么问题

在企业级应用中，异常消息往往需要面向不同语言和地区的用户展示本地化内容。直接在代码中硬编码中文或英文错误提示存在以下痛点：

- **多语言维护困难**：错误消息散落在业务代码各处，新增语言时需要逐一修改源码。
- **异常与消息耦合**：`RuntimeException` 只接受字符串消息，无法携带错误码进行统一拦截和翻译。
- **占位符参数化缺失**：部分错误消息需要动态插入变量（如用户名、金额），拼接字符串容易出错且不利于翻译。

`locale-message` 能力通过 `LocaleMessageException` + Spring `MessageSource` 的组合，将错误码作为异常的语义标识，在抛出时自动根据当前请求的 `Locale` 从 `messages.properties` 资源文件中解析出对应的本地化消息，从而实现异常消息与业务代码的解耦和多语言支持。

## 如何使用

### 核心组件

| 类 | 职责 |
|----|------|
| `LocaleMessageException` | 国际化异常，支持仅传错误码、错误码+占位符参数、错误码+自定义消息等多种构造方式 |
| `LocaleMessage` | 封装 Spring `MessageSource`，提供按 code + args + locale 解析消息的能力 |
| `MessageContext` | 单例上下文，持有 `LocaleMessage` 实例，供异常类在静态上下文中获取本地化消息 |

### 配置消息资源文件

在 `src/main/resources/` 下创建标准 Spring 消息资源文件：

```properties
# messages.properties（默认/回退）
error.user.not.found=User not found: {0}
error.param.invalid=Invalid parameter: {0}, expected {1}

# messages_zh_CN.properties
error.user.not.found=用户不存在: {0}
error.param.invalid=参数无效: {0}，期望值 {1}
```

Spring Boot 会自动加载这些文件作为 `MessageSource`。

### 抛出国际化异常

`LocaleMessageException` 提供多种构造方式和静态工厂方法：

```java
// 1. 仅传错误码 —— 自动从 messages.properties 解析消息
throw new LocaleMessageException("error.user.not.found");

// 2. 错误码 + 占位符参数
throw LocaleMessageException.of("error.user.not.found", "zhangsan");

// 3. 错误码 + 多个占位符参数
throw LocaleMessageException.of("error.param.invalid", "age", "Integer");

// 4. 错误码 + 自定义消息（不走 MessageSource）
throw new LocaleMessageException("ERR_CUSTOM", "自定义错误消息");

// 5. 携带原始异常
throw new LocaleMessageException("error.user.not.found", cause);
```

### 自动装配

框架启动时，`LocaleMessage` Bean 会被创建并调用 `init()` 方法，将自身注册到 `MessageContext` 单例中。`LocaleMessageException` 在构造时通过 `MessageContext.getInstance().getErrorMsg(errCode, args)` 获取本地化消息，整个过程对业务代码透明，无需手动注入任何 Bean。

当前请求的 `Locale` 由 Spring 的 `LocaleContextHolder` 提供（通常通过 `Accept-Language` 请求头或 `LocaleChangeInterceptor` 设置）。

## 使用实例

### 完整示例：用户查询服务

```java
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User getUser(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() ->
                LocaleMessageException.of("error.user.not.found", username)
            );
    }

    public void updateUserAge(String username, int age) {
        if (age < 0 || age > 150) {
            throw LocaleMessageException.of("error.param.invalid", "age", "0~150");
        }
        // ...
    }
}
```

当客户端发送 `Accept-Language: zh-CN` 请求时，若用户不存在，返回的异常消息为 `"用户不存在: zhangsan"`；若未指定语言或使用默认 locale，则返回 `"User not found: zhangsan"`。

### 在全局异常处理器中统一捕获

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LocaleMessageException.class)
    public Response handleLocaleException(LocaleMessageException e) {
        return Response.buildFailure(e.getErrCode(), e.getErrMessage());
    }
}
```

由于异常对象已经携带了本地化后的 `errMessage`，全局处理器只需直接提取即可，无需再做二次翻译。

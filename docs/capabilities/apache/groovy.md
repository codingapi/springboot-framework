---
name: apache/groovy
module: apache
description: Groovy 动态脚本语言，运行在 JVM 上，支持动态类型和脚本化编程
status: 已实现
scope: 后端
source: 框架:apache
import: "org.apache.groovy:groovy"
framework_version: 4.0.24
---

## 解决什么问题

在企业级应用中，许多业务逻辑需要在不重启服务的前提下进行动态调整，例如：

- **动态规则引擎**：促销折扣、风控策略、审批条件等频繁变更的业务规则，硬编码会导致频繁发版。
- **可配置的计算逻辑**：报表字段计算、数据转换、自定义校验等逻辑需要由运营人员或实施人员灵活配置。
- **热更新与灰度验证**：线上问题修复或新逻辑验证时，希望以脚本方式快速下发并即时生效，而非走完整的发布流程。
- **降低扩展门槛**：让非核心开发人员（如实施顾问、数据分析师）也能通过简洁的 DSL 参与业务逻辑编写。

Groovy 作为运行在 JVM 上的动态语言，与 Java 完全兼容且语法更简洁。springboot-framework 的 `springboot-starter-script` 模块在此基础上封装了完整的脚本运行时，提供了编译缓存、LRU 淘汰、事务绑定、变量注入等企业级能力，使 Groovy 脚本能够安全、高效地嵌入到 Spring Boot 应用中。

## 如何使用

### 1. 引入依赖

在需要使用脚本能力的模块中添加 `springboot-starter-script` 依赖，该模块已包含 `org.apache.groovy:groovy:4.0.24`：

```xml
<dependency>
    <groupId>com.codingapi.springboot</groupId>
    <artifactId>springboot-starter-script</artifactId>
</dependency>
```

### 2. 核心 API

框架通过 `GroovyScriptRuntimeContext` 单例提供脚本运行时入口，主要方法如下：

| 方法 | 说明 |
|------|------|
| `compile(script, cache)` | 编译脚本，`cache=true` 时使用 SHA256 作为 key 进行 LRU 缓存 |
| `run(script, returnType, transactionMode, binds)` | 执行整段脚本并返回结果 |
| `invoke(method, script, returnType, transactionMode, binds, args)` | 执行脚本中指定函数并返回结果 |
| `clearCache()` | 清空已编译的脚本缓存 |
| `cacheSize()` | 获取当前缓存中的脚本数量 |

### 3. 高级封装：GroovyScript

对于需要持久化管理的脚本，可使用 `GroovyScript` Builder 构建脚本对象，它集成了编译、执行、存储、元数据扫描等完整生命周期：

```java
GroovyScript script = GroovyScript.builder("discount-rule-v1")
        .script("def calculate(price, rate) { return price * rate }")
        .method("calculate")
        .returnType(BigDecimal.class)
        .description("折扣计算脚本")
        .tag("promotion")
        .build();
```

关键操作：

- `script.compile(true)` — 预编译并缓存
- `script.invoke(binds, args...)` — 调用指定函数
- `script.run(binds)` — 执行整段脚本
- `script.save()` — 持久化到 Repository
- `script.temp()` — 存入临时上下文（定时清理）
- `script.toMetadata()` — 提取脚本元数据信息

### 4. 事务模式

脚本执行支持三种事务模式，通过 `TransactionMode` 枚举控制：

- `DEFAULT` — 不参与事务管理，直接执行
- `READONLY` — 在只读事务中执行，适用于查询类脚本
- `COMMIT` — 在可提交事务中执行，适用于涉及数据写入的脚本

### 5. 变量绑定

通过 `binds` Map 向脚本注入外部变量，脚本内可直接按变量名访问：

```java
Map<String, Object> binds = new HashMap<>();
binds.put("userService", userService);
binds.put("orderId", orderId);

Object result = script.invoke(binds, param1, param2);
```

### 6. 缓存机制

- **编译缓存**：`GroovyScriptRuntime` 内部使用 `LinkedHashMap` 实现 LRU 缓存，最大容量通过配置项 `shellMaxCacheSize` 控制，超出时自动淘汰最久未使用的脚本。
- **脚本对象缓存**：`GroovyScriptCacheContext` 维护脚本定义的 LRU 缓存（上限 10240），查找顺序为：内存缓存 → 临时上下文 → Repository 持久层。

## 使用实例

### 示例 1：执行简单计算脚本

```java
// 直接运行一段 Groovy 脚本
String script = """
    def total = items.stream()
        .mapToDouble { it.price * it.quantity }
        .sum()
    return BigDecimal.valueOf(total).setScale(2, RoundingMode.HALF_UP)
    """;

Map<String, Object> binds = Map.of("items", orderItems);
BigDecimal total = GroovyScriptRuntimeContext.getInstance()
        .run(script, BigDecimal.class, TransactionMode.READONLY, binds);
```

### 示例 2：调用脚本中的函数

```java
String script = """
    def checkEligibility(user, minScore) {
        return user.score >= minScore && user.status == 'ACTIVE'
    }
    """;

Map<String, Object> binds = Map.of("minScore", 80);
Boolean eligible = GroovyScriptRuntimeContext.getInstance()
        .invoke("checkEligibility", script, Boolean.class,
                TransactionMode.DEFAULT, binds, currentUser);
```

### 示例 3：使用 GroovyScript 管理持久化脚本

```java
// 构建并保存脚本
GroovyScript script = GroovyScript.builder("tax-calc-2024")
        .script("""
            def calcTax(amount, region) {
                def rate = region == 'CN' ? 0.13 : 0.08
                return (amount * rate).setScale(2, RoundingMode.HALF_UP)
            }
            """)
        .method("calcTax")
        .returnType(BigDecimal.class)
        .description("2024年税率计算")
        .tag("finance")
        .build();

script.save();       // 持久化
script.compile(true); // 预编译并缓存

// 后续使用时从缓存获取并执行
GroovyScript cached = GroovyScriptCacheContext.getInstance()
        .getGroovyScript("tax-calc-2024");
BigDecimal tax = cached.invoke(TransactionMode.READONLY, null,
        new BigDecimal("10000"), "CN");
// tax = 1300.00
```

### 示例 4：在事务中执行数据写入脚本

```java
String script = """
    def user = userRepository.findByUsername(username)
    if (user != null) {
        user.lastLoginTime = new Date()
        user.loginCount += 1
        userRepository.save(user)
        return true
    }
    return false
    """;

Map<String, Object> binds = Map.of(
        "userRepository", userRepository,
        "username", "admin"
);

Boolean updated = GroovyScriptRuntimeContext.getInstance()
        .run(script, Boolean.class, TransactionMode.COMMIT, binds);
```

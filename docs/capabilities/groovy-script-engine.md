---
name: groovy-script-engine
description: Groovy 脚本运行时编译、缓存与热更新引擎，提供 REST API 管理脚本生命周期
status: 已实现
scope: 后端
source: 项目自有
---

## 解决什么问题

在企业应用中，部分业务规则（如流程节点的标题生成、操作者匹配、数据校验等）需要在不重启服务的情况下动态调整。硬编码这些规则会导致每次变更都需要重新部署，而使用配置化方案又难以表达复杂逻辑。

`springboot-starter-script` 提供了一个基于 Groovy 的脚本引擎，支持：

- **运行时编译**：通过 `GroovyScript.compile(cache)` 在运行时编译 Groovy 脚本
- **LRU 缓存**：编译后的脚本对象通过 `GroovyScriptRuntimeContext` 缓存，避免重复编译
- **热更新**：修改脚本后重新调用 `compile(true)` 即可生效，无需重启
- **元数据扫描**：通过注解（`@ScriptType`、`@ScriptField`、`@ScriptFunction`、`@ScriptParameter`）自动提取脚本的类型、字段、方法等元数据信息
- **策略扩展**：通过 `GroovyTypeFixStrategy`、`GroovyMetadataGenerateStrategy`、`ScriptTypeMapping` 三种策略接口扩展脚本处理逻辑
- **REST API**：内置 `GroovyScriptController` 提供脚本编译、查询、保存等 HTTP 接口
- **临时脚本**：支持临时存储和定时清理机制，方便开发调试

## 如何使用

### 1. 引入依赖

```xml
<dependency>
    <groupId>com.codingapi.springboot</groupId>
    <artifactId>springboot-starter-script</artifactId>
</dependency>
```

### 2. 配置属性

在 `application.properties` 中配置脚本引擎参数：

```properties
# 脚本引擎配置前缀
codingapi.script.*=...
```

配置通过 `GroovyScriptProperties` 绑定，由 `AutoConfiguration` 自动注册。

### 3. 创建和执行脚本

使用 `GroovyScript.Builder` 构建脚本对象：

```java
GroovyScript script = GroovyScript.builder("my_script_key")
    .script("return 'Hello, ' + name")
    .description("问候脚本")
    .build();

// 编译脚本（启用缓存）
script.compile(true);

// 执行脚本
Map<String, Object> binds = Map.of("name", "World");
String result = script.invoke(TransactionMode.NONE, binds);
```

核心 API：
- `GroovyScript.builder(key)` — 创建构建器
- `builder.script(String)` — 设置脚本内容
- `builder.description(String)` — 设置描述
- `script.compile(boolean cache)` — 编译脚本，`cache=true` 时缓存编译结果
- `script.invoke(TransactionMode, Map<String,Object> binds, Object... requests)` — 执行脚本
- `script.save()` — 持久化脚本到仓储
- `script.temp()` — 临时存储脚本（会被定时清理）
- `script.toMetadata()` — 提取脚本元数据

### 4. 脚本元数据注解

通过注解为脚本添加结构化元数据：

```groovy
@ScriptType(name = "LeaveApproval", description = "请假审批脚本")
class LeaveApproval {

    @ScriptField(description = "请假天数")
    int days

    @ScriptFunction(description = "计算审批级别")
    int calculateLevel(
        @ScriptParameter(description = "员工职级") int rank
    ) {
        return rank > 5 ? 2 : 1
    }
}
```

框架通过 `GroovyMetadataScannerUtils.scanner(groovyScript)` 自动扫描这些注解，生成 `GroovyMetadata` 对象，包含 `GroovyType`、`GroovyField`、`GroovyFunction` 等信息。

### 5. 扩展策略

#### 类型修复策略 (GroovyTypeFixStrategy)

当需要对特定类型的脚本进行额外处理时：

```java
GroovyTypeFixStrategyContext.getInstance().addFixStrategy(
    new GroovyTypeFixStrategy() {
        @Override
        public boolean support(Class<?> clazz) {
            return MyInterface.class.isAssignableFrom(clazz);
        }

        @Override
        public void fix(GroovyScript groovyScript, GroovyType groovyType) {
            // 对 groovyType 进行修复或增强
        }
    }
);
```

#### 元数据生成策略 (GroovyMetadataGenerateStrategy)

自定义元数据生成逻辑（优先于注解扫描）：

```java
GroovyMetadataGenerateStrategyContext.getInstance().addGenerateStrategy(
    new GroovyMetadataGenerateStrategy() {
        @Override
        public boolean support(GroovyScript groovyScript) {
            return "special_key".equals(groovyScript.getKey());
        }

        @Override
        public GroovyMetadata generate(GroovyScript groovyScript) {
            // 手动构建元数据
            return customMetadata;
        }
    }
);
```

#### 类型映射策略 (ScriptTypeMapping)

将脚本中的类型映射为目标类型：

```java
ScriptTypeMappingContext.getInstance().addMapping(
    new ScriptTypeMapping() {
        @Override
        public boolean support(Class<?> target) {
            return target == OriginalType.class;
        }

        @Override
        public Class<?> mapping(Class<?> target) {
            return MappedType.class;
        }
    }
);
```

### 6. REST API

框架内置了 `GroovyScriptController`，提供以下端点：

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/groovy-script/compile` | 编译脚本 |
| GET | `/api/groovy-script/getScript?key=xxx` | 获取脚本内容 |
| GET | `/api/groovy-script/getMetadata?key=xxx` | 获取脚本元数据 |
| POST | `/api/groovy-script/save` | 保存脚本 |

编译请求体 (`ScriptCompileRequest`)：
```json
{
    "script": "return 'hello'",
    "cache": true
}
```

保存请求体 (`ScriptSaveRequest`)：
```json
{
    "key": "my_script_key",
    "script": "return 'updated content'"
}
```

## 使用实例

### 脚本生命周期管理

```java
// 1. 创建脚本
GroovyScript script = GroovyScript.builder("order_validator")
    .script("""
        boolean validate(Map order) {
            return order.amount > 0 && order.items != null
        }
    """)
    .description("订单校验脚本")
    .build();

// 2. 临时存储（用于开发调试，会被定时清理）
script.temp();

// 3. 编译并缓存
script.compile(true);

// 4. 执行
Map<String, Object> binds = Map.of("order", orderData);
Boolean valid = script.invoke(TransactionMode.NONE, binds);

// 5. 确认无误后持久化
script.save();
```

### 引擎启动与关闭

`GroovyScriptEngineRunner` 实现了 `InitializingBean` 和 `DisposableBean`：

- **启动时**（`afterPropertiesSet`）：从 `TempGroovyScriptRepository` 加载所有临时脚本到内存缓存
- **关闭时**（`destroy`）：将内存中的临时脚本持久化到 `TempGroovyScriptRepository`

这确保了服务重启后临时脚本不会丢失。

### 策略上下文清理

所有策略上下文都提供了 `clear()` 方法，可在测试或重新初始化时清空已注册的策略：

```java
GroovyTypeFixStrategyContext.getInstance().clear();
GroovyMetadataGenerateStrategyContext.getInstance().clear();
ScriptTypeMappingContext.getInstance().clear();
```

### 自动配置

`AutoConfiguration` 类通过 `@ComponentScan(basePackages = "com.codingapi.springboot.script")` 自动扫描脚本模块的所有组件，并注册：
- `GroovyScriptEngineRunner` Bean — 负责引擎生命周期管理
- `GroovyScriptProperties` Bean — 绑定 `codingapi.script.*` 配置项

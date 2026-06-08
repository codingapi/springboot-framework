---
name: groovy-script-engine
description: Groovy 脚本运行时引擎，支持动态编译、LRU 缓存、类型映射、元数据扫描与临时脚本持久化
status: 已实现
scope: 后端
source: 项目自有
import: com.codingapi.springboot:springboot-starter-script
symbols:
  - GroovyScript
  - GroovyScriptRuntimeContext
  - GroovyScriptRuntime
  - GroovyScriptEngineRunner
  - GroovyScriptCacheContext
  - GroovyMetadataScannerUtils
  - GroovyMetadata
  - GroovyType
  - GroovyField
  - GroovyFunction
  - ScriptTypeMappingContext
  - ScriptTypeMapping
  - GroovyTypeFixStrategyContext
  - GroovyTypeFixStrategy
  - GroovyMetadataGenerateStrategyContext
  - GroovyScriptController
  - GroovyScriptRepository
  - TempGroovyScriptContext
  - TransactionMode
content_hash: 913fcdb49caf0b894a3d6cbff60bb519566caf888e292724c652766cc4fa9c90
---

## 解决什么问题

在企业应用中，某些业务规则需要动态调整而不想重新部署（如工作流条件表达式、表单校验规则、动态报表查询）。本 Groovy 脚本引擎解决了以下问题：

- **运行时编译执行**：Groovy 脚本在运行时编译为 Java 字节码并执行
- **LRU 编译缓存**：编译后的 Class 缓存在 LRU Cache 中，避免重复编译
- **类型映射**：解决 Groovy 与 Java 之间的类型差异（如 `int` → `Integer`）
- **元数据扫描**：通过注解扫描脚本的字段、函数、参数信息
- **临时脚本持久化**：临时脚本在应用重启时自动持久化到数据库并恢复

## 如何使用

### 构建与执行脚本

```java
GroovyScript script = GroovyScript.builder("calc-discount")
    .script("def calc(BigDecimal price, int level) { return price * (1 - level * 0.1) }")
    .description("计算折扣价")
    .returnType(BigDecimal.class)
    .build();

// 执行脚本
BigDecimal result = script.invoke(TransactionMode.none, 
    Map.of(), new BigDecimal("100"), 2);
// result = 80.0
```

### 带事务执行

```java
// 在 Spring 事务中执行脚本
Object result = script.invoke(TransactionMode.required, bindMap, arg1, arg2);
```

### REST API 管理脚本

引擎提供 `GroovyScriptController`，通过 HTTP 接口管理脚本的编译、保存和执行。

### 类型映射扩展

```java
// 注册自定义类型映射
ScriptTypeMappingContext.getInstance().addMapping(new ScriptTypeMapping() {
    public boolean support(Class<?> target) { return target == PageRequest.class; }
    public Class<?> mapping(Class<?> target) { return CustomPageRequest.class; }
});
```

## 使用实例

```java
// 工作流中使用 Groovy 脚本匹配审批人
GroovyScript script = GroovyScript.builder("match-operator")
    .script("""
        def matcher(session) {
            if (session.amount > 10000) return [1001L] // 总经理审批
            return [session.createOperatorId]           // 直属上级
        }
    """)
    .build();

List<Long> operatorIds = script.invoke(TransactionMode.none, Map.of(), session);
```

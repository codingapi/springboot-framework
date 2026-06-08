---
name: groovy-script-engine
description: Groovy 动态脚本引擎，支持运行时编译执行、LRU 缓存、热更新和 REST API
status: 已实现
scope: 后端
source: 项目自有
last_commit: fbfb901b
code_files:
  - springboot-starter-script/src/main/java/com/codingapi/springboot/script/GroovyScriptRuntime.java
  - springboot-starter-script/src/main/java/com/codingapi/springboot/script/GroovyScriptRuntimeContext.java
  - springboot-starter-script/src/main/java/com/codingapi/springboot/script/cache/GroovyScriptCacheContext.java
  - springboot-starter-script/src/main/java/com/codingapi/springboot/script/repository/GroovyScriptRepository.java
  - springboot-starter-script/src/main/java/com/codingapi/springboot/script/repository/TempGroovyScriptRepository.java
  - springboot-starter-script/src/main/java/com/codingapi/springboot/script/strategy/GroovyTypeFixStrategy.java
  - springboot-starter-script/src/main/java/com/codingapi/springboot/script/strategy/GroovyMetadataGenerateStrategy.java
  - springboot-starter-script/src/main/java/com/codingapi/springboot/script/controller/GroovyScriptController.java
  - springboot-starter-script/src/main/java/com/codingapi/springboot/script/runner/GroovyScriptEngineRunner.java
---

## 解决什么问题

业务规则频繁变更时，硬编码需要重新编译部署。Groovy 脚本引擎提供运行时动态编译执行能力，让业务规则可以通过脚本形式热加载，无需重启应用。典型场景：

- 动态表单校验规则
- 工作流节点审批脚本
- 数据转换/映射规则
- 临时数据处理脚本

## 如何使用

### 核心运行时（GroovyScriptRuntime）

```java
// 初始化运行时（指定 LRU 缓存最大容量）
GroovyScriptRuntime runtime = new GroovyScriptRuntime(100);

// 编译脚本（带缓存 — 以 SHA256 哈希为 Key）
runtime.compile("def add(a, b) { return a + b }", true);

// 编译脚本（不带缓存）
runtime.compile("println 'hello'", false);

// 执行脚本
Object result = runtime.execute("return 1 + 2");

// 执行脚本并绑定变量
Map<String, Object> bindings = new HashMap<>();
bindings.put("name", "张三");
Object result = runtime.execute("return 'Hello, ' + name", bindings);
```

### LRU 缓存机制

- 缓存 Key 为脚本内容的 SHA256 哈希值
- 采用 `LinkedHashMap` 实现 LRU 淘汰策略
- 超过 `maxCacheSize` 时自动移除最久未使用的条目
- 通过 `clearCache()` 手动清空缓存
- 通过 `cacheSize()` 查看当前缓存数量

### 脚本仓库（GroovyScriptRepository）

持久化存储脚本，支持 CRUD 和按名称查找：
```java
// 保存脚本
repository.save("calc_discount", scriptContent);

// 按名称执行
Object result = runtime.executeByName("calc_discount", bindings);
```

### 临时脚本（TempGroovyScriptRepository）

一次性执行的临时脚本，执行后自动清理：
```java
// 提交临时脚本
tempRepo.submit("return data.collect { it.name }", dataBindings);
```

### 元数据扫描

通过注解扫描提取脚本元数据，用于前端展示：
```java
@GroovyScript
class DiscountCalc {
    @ScriptFunction(description = "计算折扣")
    static BigDecimal calc(@ScriptParameter("金额") BigDecimal amount) {
        return amount.multiply(new BigDecimal("0.9"))
    }
}
```

### 策略扩展点

- `GroovyMetadataGenerateStrategy` — 自定义元数据生成逻辑
- `GroovyTypeFixStrategy` — 自定义类型元数据修正（如补充泛型信息）

### REST API（GroovyScriptController）

| 端点 | 方法 | 说明 |
|------|------|------|
| `/open/script/save` | POST | 保存脚本 |
| `/open/script/compile` | POST | 编译检查（不执行） |
| `/open/script/run` | POST | 执行脚本 |
| `/open/script/list` | GET | 列出所有脚本 |

## 使用实例

```java
// 1. 基本执行
GroovyScriptRuntime runtime = new GroovyScriptRuntime(50);
Object result = runtime.execute("return [1,2,3].sum()");
assert result.equals(6);

// 2. 缓存模式 — 相同内容只编译一次
String script = "def discount(amount) { return amount * 0.85 }";
runtime.compile(script, true); // 首次编译并缓存
Object r1 = runtime.execute(script); // 命中缓存
Object r2 = runtime.execute(script); // 命中缓存

// 3. 绑定变量执行
Map<String, Object> vars = new HashMap<>();
vars.put("price", new BigDecimal("100"));
vars.put("quantity", 3);
Object total = runtime.execute(
    "return price.multiply(new BigDecimal(quantity))",
    vars
);
// total = 300

// 4. 清空缓存
runtime.clearCache();
assert runtime.cacheSize() == 0;
```

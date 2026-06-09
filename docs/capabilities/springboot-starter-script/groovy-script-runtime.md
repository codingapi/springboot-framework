---
name: springboot-starter-script/groovy-script-runtime
module: springboot-starter-script
description: 基于 Groovy 的动态脚本运行时，支持函数调用与脚本执行两类入口、LRU 缓存、临时脚本落盘与启动恢复
status: 已实现
scope: 后端
source: 项目自有
import: "com.codingapi.springboot:springboot-starter-script"
symbols:
  - GroovyScript
  - GroovyScriptRuntime
  - GroovyScriptRuntimeContext
  - GroovyScriptCacheContext
  - GroovyScriptEngineRunner
content_hash: 6da4f2cb99af82f939e0cf9577a8b1f7ed442aef77e690efe3ba5aad9576123e
---

## 解决什么问题

业务系统经常需要让运营/管理员在运行时编写/修改业务规则、表单逻辑、按钮响应脚本，传统做法是发版重部署。本能力提供：

- 运行时 Groovy 脚本解析与执行（`groovy.lang.GroovyShell`）
- 函数调用与脚本执行两种入口
- LRU 缓存编译结果（SHA-256 作为 key）
- 临时脚本（`TempGroovyScript`）定时清理 + 启动恢复
- Builder 模式构建 `GroovyScript` 对象，支持元数据（描述/标签/类型）

## 如何使用

**1. 函数式调用（执行有入参出参的方法）**

```java
Map<String, Object> binds = new HashMap<>();
binds.put("user", currentUser);

String result = GroovyScriptRuntimeContext.getInstance().invoke(
    "compute",                          // 方法名
    "def compute(user){ return user.name + '_computed' }",  // 脚本
    String.class,                       // 返回类型
    TransactionMode.DEFAULT,
    binds,
    currentUser                         // 方法实参
);
```

**2. 脚本式执行（执行整个脚本体）**

```java
Map<String, Object> binds = new HashMap<>();
binds.put("x", 10);

Integer result = GroovyScriptRuntimeContext.getInstance().run(
    "return x * 2",
    Integer.class,
    TransactionMode.DEFAULT,
    binds
);
```

**3. 通过 GroovyScript 对象管理**

```java
GroovyScript script = GroovyScript.builder("order-discount")
    .script("return amount > 1000 ? amount * 0.9 : amount")
    .description("订单满 1000 打 9 折")
    .method("compute")
    .returnType(BigDecimal.class)
    .build();

script.save();  // 持久化到 repository
script.compile();  // 预编译并缓存
BigDecimal result = script.run(Map.of("amount", new BigDecimal("1500")));
```

**4. 临时脚本（重启后自动落盘恢复）**

```java
GroovyScript tempScript = GroovyScript.builder("test-temp").script("...").build();
tempScript.temp();  // 写入 TempGroovyScriptContext，应用启动时由 GroovyScriptEngineRunner 加载
```

**5. 配置缓存大小**

```yaml
codingapi:
  script:
    shell-max-cache-size: 1000   # 编译缓存上限（默认通过 PropertiesContext 读取）
```

## 使用实例

```java
// 1. 业务规则动态化（按钮脚本）
@RestController
public class DynamicButtonController {
    @PostMapping("/button/execute")
    public Object execute(@RequestParam String scriptKey, @RequestBody Map<String,Object> params) {
        GroovyScript script = GroovyScriptCacheContext.getInstance().getGroovyScript(scriptKey);
        if (script == null) {
            throw new IllegalArgumentException("script not found: " + scriptKey);
        }
        return script.run(params);
    }
}

// 2. 临时调试脚本（开发期）
// 在不重启服务的前提下，业务人员可注入测试脚本
// 重启后 TempGroovyScriptEngineRunner 会把内存中的 temp 脚本批量落盘到 DB

// 3. 配合 springboot-starter-data-fast 的 ScriptMapping 使用：
// ScriptMapping 注册脚本 key 与业务模块的映射关系，ScriptRuntime 解析并执行
```

---
name: apache-groovy
description: 动态语言运行时，支持脚本编译与执行
status: 已实现
scope: 后端
source: 框架:Apache Groovy
---

## 解决什么问题

Apache Groovy 为项目提供运行时脚本引擎能力，解决了以下问题：

- **动态逻辑**：允许在不重启应用的情况下修改业务逻辑，适用于规则引擎、动态报表等场景
- **热更新**：脚本存储在数据库中，支持运行时编译和缓存，修改即时生效
- **REST API 驱动**：通过 REST 接口管理脚本的编译和执行，便于运维操作
- **与工作流集成**：flow 模块使用 GroovyShell 执行流程条件表达式

## 如何使用

项目使用 Groovy 4.0.24，在 `springboot-starter-script` 和 `springboot-starter-flow` 两个模块中使用：

1. **GroovyScriptRuntime**：核心运行时，基于 `GroovyShell` + `GroovyClassLoader`，内置 LRU 缓存避免重复编译
2. **编译模式**：`compile(script, cache)` 支持缓存和非缓存两种模式，缓存 key 为脚本 SHA256 哈希
3. **函数调用**：`invoke(method, script, returnType, binds, args)` 支持调用脚本中定义的函数并绑定变量
4. **启动加载**：`GroovyScriptEngineRunner` 在应用启动时从数据库加载临时脚本到缓存
5. **REST API**：`GroovyScriptController` 提供脚本编译和执行的 HTTP 接口

## 使用实例

### GroovyScriptRuntime 编译与缓存

```java
public class GroovyScriptRuntime {

    private final GroovyShell shell;
    private final Map<String, Script> cache;  // LRU 缓存

    public void compile(String script, boolean cache) {
        if (cache) {
            String key = SHA256.sha256(script);
            Script runtime = this.cache.get(key);
            if (runtime == null) {
                runtime = this.shell.parse(script);
                this.cache.put(key, runtime);
            }
        } else {
            this.shell.parse(script);
        }
    }

    public <T> T invoke(String method, String script, Class<T> returnType,
                        Map<String, Object> binds, Object... args) {
        // 执行脚本中的指定函数
    }
}
```

### 启动时自动加载脚本

```java
public class GroovyScriptEngineRunner implements InitializingBean, DisposableBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        // 从数据库分页加载所有临时脚本到缓存
        PageRequest request = PageRequest.of(0, 100);
        Page<TempGroovyScript> page = TempGroovyScriptRepositoryContext.getInstance().find(request);
        while (page.hasNext()) {
            TempGroovyScriptContext.getInstance().loadAll(page.getContent());
            request = PageRequest.of(request.getCurrent() + 1, 100);
            page = TempGroovyScriptRepositoryContext.getInstance().find(request);
        }
    }
}
```

### Flow 模块中的 GroovyShell 使用

```java
// springboot-starter-flow 中使用 GroovyShell 执行流程条件脚本
public class GroovyShellContext {
    public <T> T run(String script, Class<T> returnType, Object... args) {
        // 执行流程节点的条件判断脚本
    }
}
```

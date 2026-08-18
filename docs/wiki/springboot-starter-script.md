springboot-starter-script 功能介绍

## 模块定位

`springboot-starter-script` 是框架的 Groovy 脚本引擎模块，提供以下核心能力：

1. **运行时编译**：基于 `GroovyShell` 在应用运行时动态编译 Groovy 脚本，无需重启即可变更业务逻辑；
2. **缓存**：编译后的脚本对象按脚本内容的 SHA256 摘要做 LRU 缓存，重复执行时直接命中缓存，避免重复编译；
3. **热更新**：通过 REST API 或 `GroovyScript.save()` 更新脚本内容并重新编译，实现脚本的热更新；
4. **REST API**：内置 `GroovyScriptController`，提供脚本编译、查询、保存（热更新）的 HTTP 端点；
5. **元数据扫描**：通过注解（`@ScriptType`/`@ScriptField`/`@ScriptFunction`/`@ScriptParameter`）自动扫描脚本的请求参数、绑定对象与返回类型，生成结构化的元数据，便于前端渲染脚本调用表单；
6. **事务支持**：脚本执行支持 `DEFAULT`/`COMMIT`/`READONLY` 三种事务模式。

Maven 依赖：

```xml
<!-- 脚本引擎框架 -->
<dependency>
    <groupId>com.codingapi.springboot</groupId>
    <artifactId>springboot-starter-script</artifactId>
    <version>17.3.0</version>
</dependency>
```

模块依赖 `springboot-starter`、`spring-boot-starter-web` 以及 Groovy 4.x（`groovy`、`groovy-json`、`groovy-xml`）。自动配置类为 `com.codingapi.springboot.script.AutoConfiguration`，同时注册在 `META-INF/spring.factories` 与 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 中。

### 配置项

配置前缀为 `codingapi.script`（绑定 `GroovyScriptProperties`）：

```properties
# 临时脚本到期时间（毫秒），默认15分钟 1000*60*15=900000
codingapi.script.temp-valid-time=900000
# 脚本执行对象（编译后的Script）最大缓存大小，默认 10*1024
codingapi.script.shell-max-cache-size=10240
```

## 核心类与用法

### GroovyScriptRuntime（脚本运行时）

`GroovyScriptRuntime` 是脚本的编译与执行入口，内部持有 `GroovyShell` 与一个 LRU 缓存（基于 `LinkedHashMap` 的访问顺序模式，容量由构造参数 `maxCacheSize` 控制，超出容量时自动淘汰最久未访问的条目）。

主要方法：

```java
// 按最大缓存容量构造运行时
public GroovyScriptRuntime(int maxCacheSize)

// 编译脚本；cache=true 时以脚本内容的 SHA256 为 key 缓存编译结果
public void compile(String script, boolean cache)
public void compile(String script)

// 执行脚本中的函数（method），binds 为绑定对象，args 为函数参数
public <T> T invoke(String method, String script, Class<T> returnType,
                    Map<String,Object> binds, Object... args)
public <T> T invoke(String method, String script, Class<T> returnType, Object... args)
public <T> T invoke(String method, String script, Class<T> returnType,
                    TransactionMode transactionMode, Map<String,Object> binds, Object... args)

// 直接执行整个脚本（脚本顶层语句作为执行体）
public <T> T run(String script, Class<T> returnType, Map<String,Object> binds)
public <T> T run(String script, Class<T> returnType, TransactionMode transactionMode, Map<String,Object> binds)

// 缓存维护
public void clearCache()
public int cacheSize()
public int getMaxCacheSize()
```

说明：

- `invoke`/`run` 执行时会先按 SHA256 查找缓存，未命中则编译并写入缓存，因此执行路径天然具备编译缓存能力；
- `binds` 中的每个键值会通过 `Script.setProperty(key, value)` 注入脚本，脚本中可直接以该键名访问（如 `$request`、`$repository`）；
- 事务模式由 `TransactionMode` 枚举控制，底层委托 `springboot-starter` 的 `TransactionManagerContext` 实现：
  - `DEFAULT`：不做事务处理；
  - `COMMIT`：在新事务（`PROPAGATION_REQUIRES_NEW`）中执行，正常结束提交、异常回滚；
  - `READONLY`：以只读新事务执行，结束时回滚，保证数据不被修改。

`GroovyScriptRuntimeContext` 是 `GroovyScriptRuntime` 的单例持有者（`GroovyScriptRuntimeContext.getInstance()`），运行时容量取自配置项 `codingapi.script.shell-max-cache-size`，并对外暴露 `compile`/`invoke`/`run`/`clearCache`/`cacheSize` 等委托方法。

### GroovyScript 与 Builder（脚本对象）

`GroovyScript` 是脚本的领域对象，字段如下：

| 字段 | 类型 | 说明 |
|------|------|------|
| `key` | `String`（final） | 脚本唯一编码 |
| `script` | `String` | 脚本内容 |
| `description` | `String` | 脚本描述信息 |
| `method` | `String` | 脚本主函数名称 |
| `returnType` | `Class<?>` | 返回数据类型 |
| `binds` | `Map<String, Class<?>>` | 绑定对象（键为脚本内变量名，值为类型） |
| `requests` | `Map<String, Class<?>>` | 请求参数对象（键为参数名，值为类型） |
| `typeOne` / `typeTwo` | `String` | 一级 / 二级分类 |
| `tag` | `String` | 标记参数 |
| `remark` | `String` | 备注信息 |
| `createTime` / `updateTime` | `long` | 创建 / 更新时间戳 |

通过 `GroovyScript.builder(String key)` 链式构建：

```java
String script = """
        def run(request){
            return request;
        }
        """;

GroovyScript groovyScript =
        GroovyScript.builder("invoke")
                .script(script)
                .description("返回入参本身")
                .method("run")
                .tag("123")
                .returnType(Integer.class)
                .requests(Map.of("request", Integer.class))
                .binds(Map.of("$repository", SomeRepository.class))
                .typeOne("demo").typeTwo("test").remark("备注")
                .build();
```

行为方法：

```java
// 编译（委托 GroovyScriptRuntimeContext）
groovyScript.compile();           // 编译，不缓存
groovyScript.compile(true);       // 编译并缓存

// 执行整个脚本
groovyScript.run();
groovyScript.run(Map.of("$request", 100));
groovyScript.run(TransactionMode.READONLY);

// 执行脚本主函数（method 字段指定的函数）
groovyScript.invoke(100);
groovyScript.invoke(Map.of("$repository", repository), request);
groovyScript.invoke(TransactionMode.COMMIT, Map.of("$repository", repository), request);

// 生命周期
groovyScript.temp();              // 存入临时缓存，到期自动清理
groovyScript.save();              // 持久化到仓储（并清理对应的临时数据）
groovyScript.remove();            // 从仓储与临时缓存中删除
GroovyScript copy = groovyScript.copy("newKey");  // 复制为新 key 的脚本对象

// 生成元数据
GroovyMetadata metadata = groovyScript.toMetadata();
```

### GroovyScriptCacheContext（脚本数据 LRU 缓存）

`GroovyScriptCacheContext` 是 `GroovyScript` 脚本对象的内存缓存上下文（单例），内部同样是基于访问顺序的 LRU `LinkedHashMap`，容量上限为常量 `MAX_CACHE_SIZE = 10 * 1024`，超出后自动淘汰最久未访问的脚本。

获取脚本时的查找链路为：**内存缓存 → 临时缓存（TempGroovyScriptContext） → 仓储（GroovyScriptRepositoryContext）**；从仓储命中后会回填内存缓存（临时数据不回填）。

主要方法：

```java
GroovyScriptCacheContext.getInstance().getGroovyScript(key);   // 按查找链路获取脚本
GroovyScriptCacheContext.getInstance().getScript(key);         // 获取脚本内容，不存在返回 ""
GroovyScriptCacheContext.getInstance().getGroovyMetadata(key); // 获取脚本元数据
GroovyScriptCacheContext.getInstance().save(script);           // 写入缓存
GroovyScriptCacheContext.getInstance().cache(script);          // 写入缓存
GroovyScriptCacheContext.getInstance().remove(key);            // 删除缓存
GroovyScriptCacheContext.getInstance().keys();                 // 所有缓存 key
GroovyScriptCacheContext.getInstance().count();                // 缓存数量
GroovyScriptCacheContext.getInstance().setBatchCache(list);    // 批量写入缓存
GroovyScriptCacheContext.getInstance().compileAll(true);       // 批量编译缓存中的脚本
GroovyScriptCacheContext.getInstance().clear();                // 清空缓存
```

### TempGroovyScriptContext（临时脚本）

临时脚本用于"编辑中尚未保存"的草稿场景：脚本通过 `groovyScript.temp()` 进入临时缓存后，会在到期时间（配置项 `codingapi.script.temp-valid-time`，默认 15 分钟）后被自动清理。

实现要点：

- `TempGroovyScript` 包装了 `GroovyScript` 与到期时间戳 `clearTime`，提供 `isExpired()` 判断；
- `TempGroovyScriptContext` 所有临时脚本共用**一个** daemon 调度线程（`temp-groovy-script-clear` 的 `ScheduledExecutorService`）执行到期清理，避免为每个脚本创建线程造成线程泄漏；
- 重复 `save` 同一 key 时会取消旧的清理任务、重新计时；到期清理采用原子的 `remove(key, value)` 判断，防止误删已被刷新的脚本。

主要方法：

```java
TempGroovyScriptContext.getInstance().save(groovyScript);  // 写入临时缓存并开始计时
TempGroovyScriptContext.getInstance().getGroovyScript(key); // 获取临时脚本（不存在或已过期返回 null）
TempGroovyScriptContext.getInstance().count();              // 当前临时脚本数量
TempGroovyScriptContext.getInstance().findAll();            // 所有临时脚本（含到期时间）
TempGroovyScriptContext.getInstance().remove(key);          // 删除临时脚本（并取消清理任务）
TempGroovyScriptContext.getInstance().loadAll(list);        // 启动时批量加载（过期的直接剔除）
TempGroovyScriptContext.getInstance().clear();              // 清空
```

### GroovyMetadataScannerUtils（元数据与注解扫描）

`GroovyMetadataScannerUtils.scanner(GroovyScript)` 用于为脚本生成元数据 `GroovyMetadata`，`GroovyScript.toMetadata()` 即委托该工具。扫描流程：

1. 先尝试自定义元数据策略 `GroovyMetadataGenerateStrategyContext`，若存在匹配策略则直接返回策略生成的元数据；
2. 否则通过反射扫描 `requests`（请求参数）、`binds`（绑定对象）、`returnType`（返回类型）涉及的 Java 类型，递归收集类型上声明的脚本注解信息；简单类型（如 `Integer`、`String`）不展开，并通过扫描历史防止循环引用。

扫描依赖的注解（位于 `com.codingapi.springboot.script.annotation` 包）：

| 注解 | 作用目标 | 属性 | 说明 |
|------|----------|------|------|
| `@ScriptType` | 类型（TYPE） | `description` | 描述一个参与脚本交互的 Java 类型 |
| `@ScriptField` | 字段 / 方法（FIELD, METHOD） | `name`、`description` | 声明该字段可被脚本访问 |
| `@ScriptFunction` | 方法（METHOD） | `name`（必填）、`description` | 声明该方法可被脚本调用 |
| `@ScriptParameter` | 参数（PARAMETER） | `name`、`description` | 描述 `@ScriptFunction` 方法的参数 |

注解使用示例（取自模块测试代码）：

```java
@ScriptType(description = "test")
public class MyTest {

    @ScriptField(name = "id", description = "id")
    private Long id;

    @ScriptField(name = "name", description = "name")
    private String name;
}

public class MyScriptRequest extends BaseRequest {

    @ScriptField(name = "count", description = "总数量")
    private final int count;

    @ScriptFunction(name = "isSupport", description = "是否匹配")
    public boolean isSupport(@ScriptParameter(description = "总数") int count) {
        return this.count == count;
    }
}
```

生成的 `GroovyMetadata` 结构：

```java
public class GroovyMetadata {
    private final List<GroovyField> requests;    // 请求参数列表
    private final List<GroovyField> binds;       // 绑定对象列表
    private final String mainMethod;             // 主函数名称
    private final String returnType;             // 返回类型名称
    private final Map<String, GroovyType> types; // 涉及的数据类型（含 fields 与 functions）
    private final String description;            // 脚本说明
}
```

其中 `GroovyType` 描述一个数据类型（含 `fields` 字段列表与 `functions` 函数列表），`GroovyField` 描述单个字段/参数（`name`、`description`、`dataType`），`GroovyFunction` 描述可调用的函数（`name`、`description`、`returnType`、`parameters`）。

元数据扫描提供三个扩展点（均为单例上下文 + 策略接口）：

| 扩展点 | 接口 | 注册方法 | 用途 |
|--------|------|----------|------|
| 类型映射 | `ScriptTypeMapping`（`support` / `mapping`） | `ScriptTypeMappingContext.getInstance().addMapping(...)` | 将元数据中的类型映射为其他类型，例如把 `Integer` 显示为 `int` |
| 元数据调整 | `GroovyTypeFixStrategy`（`support` / `fix(GroovyScript, GroovyType)`） | `GroovyTypeFixStrategyContext.getInstance().addFixStrategy(...)` | 在类型扫描完成后调整/补充该类型的元数据 |
| 元数据生成 | `GroovyMetadataGenerateStrategy`（`support` / `generate`） | `GroovyMetadataGenerateStrategyContext.getInstance().addGenerateStrategy(...)` | 完全自定义元数据，命中后跳过注解扫描 |

### @GroovyScript 注解与字段扫描

`@GroovyScript`（作用于字段）用于标记对象中"存放脚本 key"的字段。`GroovyScriptAnnotationScannerUtils.findGroovyScriptFields(Object target)` 可以递归收集对象下所有被 `@GroovyScript` 标记字段的值（即脚本 key 列表），并支持批量更新：

```java
// Node.script 字段标注了 @GroovyScript
List<String> keys = GroovyScriptAnnotationScannerUtils
        .findGroovyScriptFields(workflow).getKeys();

// 批量更新脚本 key
GroovyScriptFieldResult result = GroovyScriptAnnotationScannerUtils
        .findGroovyScriptFields(workflow);
result.update(value -> "K123456");
```

该能力可用于工作流等场景：业务对象持有若干脚本 key 引用，保存/复制业务对象时可以整体迁移其关联的脚本。

### GroovyScriptEngineRunner（启动加载与停机持久化）

`GroovyScriptEngineRunner` 实现了 `InitializingBean` 与 `DisposableBean`，由 `AutoConfiguration` 以 `tempClearRunner` 名称注册为 Bean：

- **启动时**（`afterPropertiesSet`）：从 `TempGroovyScriptRepositoryContext` 分页读取临时脚本数据，批量加载到 `TempGroovyScriptContext`（已过期的数据在加载时剔除），恢复停机前的临时脚本；
- **停机时**（`destroy`）：将 `TempGroovyScriptContext` 中仍有效的临时脚本写回仓储，保证临时脚本跨重启不丢失。

## REST API

`GroovyScriptController` 提供脚本管理的 HTTP 端点，基础路径为 `/api/groovy-script`：

| 端点 | 方法 | 请求 | 响应 | 说明 |
|------|------|------|------|------|
| `/api/groovy-script/compile` | POST | `ScriptCompileRequest`：`{"cache": true, "script": "脚本内容"}` | `Response` | 编译脚本；`cache=true` 时缓存编译结果。编译失败抛出 `LocaleMessageException("script.compile.error")` |
| `/api/groovy-script/getScript` | GET | Query 参数 `key` | `SingleResponse<String>` | 按 key 获取脚本内容；不存在抛出 `LocaleMessageException("script.null")` |
| `/api/groovy-script/getMetadata` | GET | Query 参数 `key` | `SingleResponse<GroovyMetadata>` | 按 key 获取脚本元数据；不存在抛出 `LocaleMessageException("script.null")` |
| `/api/groovy-script/save` | POST | `ScriptSaveRequest`：`{"key": "脚本key", "script": "新脚本内容"}` | `Response` | 热更新脚本（见下文流程） |

`save` 接口的热更新流程：

1. 先在临时缓存中查找该 key：命中则更新脚本内容 → `compile(true)` 编译并缓存 → `temp()` 继续作为临时脚本保存；
2. 临时缓存未命中则在脚本缓存/仓储中查找：命中则更新脚本内容 → `compile(true)` → `save()` 持久化；
3. 两处均未命中则抛出 `LocaleMessageException("script.null", "脚本对象不存在")`；编译异常统一抛出 `LocaleMessageException("script.compile.error")`。

## 仓储扩展

脚本的持久化通过仓储接口抽象，默认提供内存实现，可按需替换为数据库等自定义实现。

### GroovyScriptRepository（正式脚本仓储）

```java
public interface GroovyScriptRepository {

    void save(GroovyScript groovyScript);

    void delete(String key);

    GroovyScript get(String key);
}
```

默认实现 `DefaultGroovyScriptRepository` 基于内存 `HashMap` 存储。通过 `GroovyScriptRepositoryContext`（单例）访问与替换：

```java
// 获取脚本（GroovyScript.save()/remove() 内部也走该上下文）
GroovyScript script = GroovyScriptRepositoryContext.getInstance().get(key);

// 替换为自定义实现（如 JPA 持久化）
GroovyScriptRepositoryContext.getInstance()
        .setGroovyScriptRepository(new MyJpaGroovyScriptRepository());
```

### TempGroovyScriptRepository（临时脚本仓储）

```java
public interface TempGroovyScriptRepository {

    void save(TempGroovyScript tempGroovyScript);

    void delete(String key);

    TempGroovyScript get(String key);

    Page<TempGroovyScript> find(PageRequest request);
}
```

默认实现 `DefaultTempGroovyScriptRepository` 基于内存 `HashMap` 存储，`find` 按 `clearTime` 排序并返回 `PageImpl` 分页结果。通过 `TempGroovyScriptRepositoryContext`（单例）访问与替换：

```java
TempGroovyScriptRepositoryContext.getInstance()
        .setTempGroovyScriptRepository(new MyTempGroovyScriptRepository());
```

`GroovyScriptEngineRunner` 启动时即通过该上下文分页加载临时脚本（每页 100 条）。

## 与其他模块的关系

- 依赖 `springboot-starter`：统一响应封装（`Response`/`SingleResponse`）、国际化异常（`LocaleMessageException`）、分页（`PageRequest`）、事务管理（`TransactionManagerContext`）、SHA256 摘要与反射注解扫描工具等基础能力均来自核心模块；
- `springboot-starter-data-fast` 模块中的 `ScriptRuntime` 会为本模块的脚本运行时绑定 `$request`/`$jdbc`/`$jpa` 等数据访问对象，属于 data-fast 的能力范畴，此处不再展开。

## 完整示例

以下示例参考模块测试代码（`GroovyScriptRuntimeContextTest`），无需 Spring 上下文即可编译运行：

```java
import com.codingapi.springboot.script.GroovyScript;

public class GroovyScriptDemo {

    public static void main(String[] args) {
        // 1. 函数式脚本：通过 invoke 调用脚本中的 run 函数
        String invokeScript = """
                def run(request){
                    return request;
                }
                """;

        GroovyScript invokeDemo =
                GroovyScript.builder("invoke-demo")
                        .script(invokeScript)
                        .description("返回入参本身")
                        .method("run")
                        .returnType(Integer.class)
                        .build();

        int result = invokeDemo.invoke(100);
        System.out.println(result);  // 100

        // 2. 直接执行脚本：通过 run 执行整个脚本，binds 注入变量
        String runScript = " return $request; ";

        GroovyScript runDemo =
                GroovyScript.builder("run-demo")
                        .script(runScript)
                        .returnType(Integer.class)
                        .build();

        int value = runDemo.run(java.util.Map.of("$request", 100));
        System.out.println(value);   // 100
    }
}
```

在 Spring Boot 环境中（例如需要事务模式或 JPA 仓储绑定时），可参考测试类 `TransactionGroovyScriptRuntimeContextTest` 的写法：

```java
GroovyScript groovyScript =
        GroovyScript.builder("transactionCommitRun")
                .script("""
                        def run(request){
                            request.addData($repository);
                        }
                        """)
                .method("run")
                .returnType(Void.class)
                .binds(Map.of("$repository", myTestRepository.getClass()))
                .requests(Map.of("request", MyScriptRequest.class))
                .build();

// 在新事务中提交执行
groovyScript.invoke(TransactionMode.COMMIT, Map.of("$repository", myTestRepository), request);
```

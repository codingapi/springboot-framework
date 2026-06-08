---
name: dynamic-mvc-mapping
description: 运行时动态注册/注销 Spring MVC REST 端点，支持将 Groovy 脚本绑定到动态 API 路径
status: 已实现
scope: 后端
source: 项目自有
last_commit: d5569b1d
code_files:
  - springboot-starter-data-fast/src/main/java/com/codingapi/springboot/fast/mapping/FastMvcMappingRegister.java
  - springboot-starter-data-fast/src/main/java/com/codingapi/springboot/fast/script/FastScriptMappingRegister.java
  - springboot-starter-data-fast/src/main/java/com/codingapi/springboot/fast/script/ScriptMapping.java
  - springboot-starter-data-fast/src/main/java/com/codingapi/springboot/fast/script/ScriptRuntime.java
---

## 解决什么问题

传统 Spring MVC 的 REST 端点在编译期通过注解（`@RequestMapping`）静态声明，无法在运行时按需增减。当业务需要以下场景时，静态映射无法满足：

- **低代码/无代码平台**：用户通过界面配置 API，后端需动态生成对应的 REST 端点
- **Groovy 脚本绑定 API**：将动态编写的 Groovy 脚本挂载到指定 URL 路径，作为即时可用的 REST 接口
- **插件化扩展**：第三方模块在运行时注册自己的 API，卸载时自动注销
- **灰度/临时接口**：在不重启应用的前提下快速上线或下线调试端点

本能力封装了 Spring `RequestMappingHandlerMapping` 的底层 API，提供安全的运行时端点注册/注销操作，并与 Groovy 脚本引擎集成，实现"脚本即 API"的动态绑定。

## 如何使用

### 核心组件

| 类 | 说明 |
|----|------|
| `FastMvcMappingRegister` | 底层 MVC 映射注册器，直接操作 `RequestMappingHandlerMapping` |
| `FastScriptMappingRegister` | 上层脚本映射注册器，将 `ScriptMapping` 绑定为 REST 端点 |
| `ScriptMapping` | 脚本与 URL 的绑定关系，持有映射路径、HTTP 方法和 Groovy 脚本内容 |
| `ScriptRuntime` | 脚本执行运行时（单例），向脚本注入 `$request`、`$jpa`、`$jdbc` 上下文变量 |
| `ScriptMethod` | HTTP 方法枚举（GET / POST），转换为 Spring `RequestMethod` |
| `ScriptRequest` | 对 `HttpServletRequest` 的类型安全封装，提供带默认值的参数获取方法 |

### 自动配置

`DataFastConfiguration` 在检测到 `WebMvcConfigurer` 类存在时自动注册以下 Bean：

- `FastMvcMappingRegister` — 注入 Spring 容器的 `requestMappingHandlerMapping`
- `FastScriptMappingRegister` — 依赖 `FastMvcMappingRegister`

无需手动配置，引入 `springboot-starter-data-fast` 模块即可使用。

### FastMvcMappingRegister API

```java
// 注册端点
void addMapping(String url, RequestMethod requestMethod, Object handler, Method method)

// 注销端点
void removeMapping(String url, RequestMethod requestMethod)
```

所有端点默认产出 `application/json` 媒体类型，使用 `PathPatternParser` 解析路径模式。

### FastScriptMappingRegister API

```java
// 将脚本映射注册为 REST 端点
void addMapping(ScriptMapping scriptMapping)

// 测试脚本映射（不注册端点，直接执行并返回结果）
Response test(ScriptMapping scriptMapping)

// 注销脚本映射对应的端点
void removeMapping(String url, ScriptMethod scriptMethod)
```

### ScriptRuntime 注入变量

脚本执行时自动绑定以下变量：

| 变量名 | 类型 | 说明 |
|--------|------|------|
| `$request` | `ScriptRequest` | 当前 HTTP 请求的类型安全封装 |
| `$jpa` | `JpaQuery` | JPA 查询工具 |
| `$jdbc` | `JdbcQuery` | JDBC 查询工具 |

### ScriptRequest 参数获取

```java
String getParameter(String key, String defaultValue)
int getParameter(String key, int defaultValue)
float getParameter(String key, float defaultValue)
double getParameter(String key, double defaultValue)
long getParameter(String key, long defaultValue)
boolean getParameter(String key, boolean defaultValue)
PageRequest pageRequest(int pageNumber, int pageSize)
```

## 使用实例

### 1. 注册一个动态 GET 端点

```java
@Autowired
private FastScriptMappingRegister scriptMappingRegister;

// 创建脚本映射：GET /api/dynamic/hello → 返回字符串
ScriptMapping mapping = new ScriptMapping(
    "/api/dynamic/hello",
    ScriptMethod.GET,
    "return 'Hello, Dynamic API!'"
);

// 注册为 REST 端点
scriptMappingRegister.addMapping(mapping);
// 此时 GET /api/dynamic/hello 已可用，返回 SingleResponse{"Hello, Dynamic API!"}
```

### 2. 注册一个带参数的 POST 端点

```java
String script = """
    def name = $request.getParameter('name', 'World')
    def age = $request.getParameter('age', 0)
    return [name: name, age: age]
""";

ScriptMapping mapping = new ScriptMapping(
    "/api/dynamic/greet",
    ScriptMethod.POST,
    script
);
scriptMappingRegister.addMapping(mapping);
```

### 3. 测试脚本（不注册端点）

```java
ScriptMapping mapping = new ScriptMapping(
    "/test",
    ScriptMethod.GET,
    "return [1, 2, 3]"
);

// 直接执行脚本并获取结果，不会注册到 MVC
Response result = scriptMappingRegister.test(mapping);
```

### 4. 注销动态端点

```java
// 移除之前注册的端点
scriptMappingRegister.removeMapping("/api/dynamic/hello", ScriptMethod.GET);
// GET /api/dynamic/hello 不再可用
```

### 5. 脚本中使用 JPA/JDBC 查询

```java
String script = """
    def page = $request.pageRequest(0, 20)
    def users = $jpa.query('select u from User u', page)
    return users
""";

ScriptMapping mapping = new ScriptMapping(
    "/api/dynamic/users",
    ScriptMethod.GET,
    script
);
scriptMappingRegister.addMapping(mapping);
```

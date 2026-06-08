---
name: spring-web-mvc
description: Web MVC 框架，提供 RESTful API 路由、请求参数绑定、响应序列化
status: 已实现
scope: 后端
source: 框架:Spring Web MVC
---

## 解决什么问题

框架需要提供 RESTful API 能力：
- 安全模块暴露版本查询接口
- Groovy 脚本模块暴露脚本编译/保存/查询接口
- 统一响应体（Response/SingleResponse/MultiResponse）的自动 JSON 序列化
- 请求参数绑定（@RequestBody、@RequestParam）

Spring Web MVC 是 Spring Boot Web 的核心组件，通过 `spring-boot-starter-web` 依赖引入。

## 如何使用

### 依赖引入

```xml
<!-- 各 starter 模块按需引入 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

### 核心注解

- `@RestController` — 声明 REST 控制器
- `@GetMapping` / `@PostMapping` — 路由映射
- `@RequestBody` — 请求体 JSON 反序列化
- `@RequestParam` — 查询参数绑定

## 使用实例

### 1. Security 版本查询接口

```java
// VersionController.java
@RestController
public class VersionController {

    @GetMapping("/open/version")
    public SingleResponse<String> version() {
        return Response.buildSuccess("3.4.54");
    }
}
```

### 2. Groovy 脚本管理接口

```java
// GroovyScriptController.java
@RestController
@RequestMapping("/api/groovy-script")
public class GroovyScriptController {

    @PostMapping("/compile")
    public Response compile(@RequestBody ScriptCompileRequest request) {
        // 编译 Groovy 脚本
        return Response.buildSuccess();
    }

    @GetMapping("/getScript")
    public SingleResponse<String> getScript(@RequestParam(name = "key") String key) {
        // 获取脚本内容
        return Response.buildSuccess(script);
    }

    @GetMapping("/getMetadata")
    public SingleResponse<GroovyMetadata> getMetadata(
            @RequestParam(name = "key") String key) {
        // 获取脚本元数据
        return Response.buildSuccess(metadata);
    }

    @PostMapping("/save")
    public Response save(@RequestBody ScriptSaveRequest request) {
        // 保存脚本
        return Response.buildSuccess();
    }
}
```

### 3. 统一响应体与 Spring MVC 集成

框架定义的 `Response`、`SingleResponse<T>`、`MultiResponse<T>` 等响应类通过 Jackson（Spring Boot 默认）自动序列化为 JSON：

```java
// Controller 返回统一响应格式
@GetMapping("/users")
public MultiResponse<User> listUsers(PageRequest request) {
    Page<User> page = userRepository.findAll(request);
    return Response.buildSuccess(page.getContent());
}
// 自动序列化为: {"success": true, "data": [...]}
```

### 涉及模块

| 模块 | 使用场景 |
|------|----------|
| springboot-starter-security | `VersionController` 版本查询 API |
| springboot-starter-script | `GroovyScriptController` 脚本管理 API |
| springboot-starter-data-fast | 动态表管理的 REST 接口（example 中使用） |

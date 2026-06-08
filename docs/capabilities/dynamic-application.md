---
name: dynamic-application
description: 支持外部 JAR 加载和 Spring 上下文热重启的应用启动器
status: 已实现
scope: 后端
source: 项目自有
last_commit: e8583c68
code_files:
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/boot/DynamicApplication.java
---

## 解决什么问题

标准 Spring Boot 应用的类路径在启动时确定，运行期间无法动态加载新的 JAR 包或模块。当业务需要以下场景时，传统启动方式无法满足：

- **插件化架构**：核心应用运行时加载外部插件 JAR，无需重新打包主应用
- **热部署/热更新**：开发或运维过程中，替换 JAR 后自动重启 Spring 上下文，减少停机时间
- **模块化分发**：不同客户/环境使用不同的 JAR 组合，通过外部目录配置而非 Maven 依赖

本能力提供了 `DynamicApplication` 启动器，替代标准的 `SpringApplication.run()`，在启动前扫描指定目录下的 JAR 文件并注入到类加载器中，同时支持运行时关闭并重建 Spring 上下文（热重启）。

## 如何使用

### 核心类

| 类 | 说明 |
|----|------|
| `DynamicApplication` | 单例应用启动器，管理外部 JAR 加载和 Spring 上下文生命周期 |

### API

```java
// 启动应用（替代 SpringApplication.run）
DynamicApplication.run(Class<?> applicationClass, String[] args)

// 热重启（关闭当前上下文 → 重新加载 JAR → 重新启动）
DynamicApplication.restart()

// 获取单例实例
DynamicApplication app = DynamicApplication.getInstance()

// 设置外部 JAR 目录（默认 ./jars）
app.setJarsFolder("/path/to/plugins")
```

### 启动流程

1. 调用 `DynamicApplication.run(App.class, args)`
2. 扫描 `jarsFolder` 目录下所有 `.jar` 文件
3. 创建 `URLClassLoader` 并将这些 JAR 加入类路径
4. 将当前线程的 ContextClassLoader 设置为该 URLClassLoader
5. 调用 `SpringApplication.run()` 启动 Spring Boot 应用

### 热重启流程

1. 调用 `DynamicApplication.restart()`
2. 在新线程中执行（非守护线程，确保 JVM 不会提前退出）
3. 关闭当前 `ConfigurableApplicationContext`
4. 重新扫描 JAR 目录并更新 ClassLoader
5. 使用原始启动类和参数重新启动 Spring Boot

### 配置

```java
// 修改外部 JAR 目录（默认为工作目录下的 ./jars）
DynamicApplication.getInstance().setJarsFolder("/opt/app/plugins");
```

### 注意事项

- 外部 JAR 目录不存在或为空时，不加载任何额外类路径，应用正常启动
- 热重启会完全销毁并重建 Spring 上下文，所有 Bean 会被重新初始化
- 热重启在新线程中执行，原调用线程立即返回
- `URLClassLoader` 以系统 ClassLoader 为父加载器，外部 JAR 中的类优先级高于系统类路径中的同名类

## 使用实例

### 1. 基本启动（替代 SpringApplication.run）

```java
@SpringBootApplication
public class MyApplication {

    public static void main(String[] args) {
        // 替代 SpringApplication.run(MyApplication.class, args);
        DynamicApplication.run(MyApplication.class, args);
    }
}
```

### 2. 自定义 JAR 目录启动

```java
@SpringBootApplication
public class MyApplication {

    public static void main(String[] args) {
        DynamicApplication.getInstance().setJarsFolder("/opt/myapp/extensions");
        DynamicApplication.run(MyApplication.class, args);
    }
}
```

### 3. 通过 REST API 触发重启

```java
@RestController
@RequestMapping("/admin")
public class AdminController {

    @PostMapping("/restart")
    public Response restart() {
        // 异步重启，接口立即返回
        DynamicApplication.restart();
        return Response.success();
    }
}
```

### 4. 目录结构示例

```
project-root/
├── jars/                    ← 默认外部 JAR 目录
│   ├── plugin-a.jar         ← 自动加载
│   ├── plugin-b.jar         ← 自动加载
│   └── readme.txt           ← 非 .jar 文件，忽略
├── src/
└── pom.xml
```

将新的插件 JAR 放入 `jars/` 目录后，调用 `DynamicApplication.restart()` 即可加载新插件并重启应用。

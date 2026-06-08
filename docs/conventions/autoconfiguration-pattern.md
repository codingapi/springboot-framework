---
name: autoconfiguration-pattern
description: 每个 starter 模块必须同时注册 spring.factories（旧版兼容）和 AutoConfiguration.imports（Spring Boot 3.x 标准），使用 @ConfigurationProperties + Context 单例模式
status: 已实现
scope: 后端
source: 项目自有
last_commit: 303b377f
code_files:
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/properties/PropertiesContext.java
  - springboot-starter/src/main/resources/META-INF/spring.factories
  - springboot-starter/src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
---

## 解决什么问题

创建 Spring Boot Starter 时，如果不遵循标准的自动配置注册规范，会导致以下问题：

1. **版本兼容性断裂**：Spring Boot 3.x 废弃了 `spring.factories` 中的 `EnableAutoConfiguration` 键，仅支持 `AutoConfiguration.imports`；但部分运行环境或工具链仍读取旧格式，缺失任一都会导致自动配置不生效
2. **配置属性无法绑定**：不使用 `@ConfigurationProperties` 注解则无法将 `application.properties` 中的配置自动映射到 Java 对象
3. **非 Spring 上下文无法访问配置**：在工具类、拦截器、代理等非 Bean 环境中，无法通过 `@Autowired` 注入配置，需要额外的 Context 单例桥接
4. **新模块遗漏注册**：没有统一的注册约定，新增 starter 时容易忘记配置文件导致功能静默失效

## 如何使用

### 1. 双文件注册（必须同时维护）

在每个 starter 模块的 `src/main/resources/META-INF/` 下创建两个文件：

**`spring.factories`**（兼容 Spring Boot 2.x 及旧工具链）：
```properties
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
com.example.mystarter.MyAutoConfiguration,\
com.example.mystarter.config.SomeConfiguration
```

**`spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`**（Spring Boot 3.x 标准）：
```
com.example.mystarter.MyAutoConfiguration
com.example.mystarter.config.SomeConfiguration
```

> ⚠️ 两个文件中列出的配置类**必须完全一致**，仅格式不同。

### 2. ConfigurationProperties + Context 模式

对于需要在非 Spring Bean 中访问的配置：

1. 定义 Properties 类并标注 `@ConfigurationProperties(prefix = "...")`
2. 定义对应的 `*Context` 单例类持有该 Properties
3. 在 `@Configuration` 类的 `@Bean` 方法中创建 Properties 并同步设置到 Context

### 3. 规则

1. **新增 starter 模块时必须同时创建两个注册文件**
2. 修改自动配置类列表时，两个文件必须同步更新
3. 配置类使用 `@Configuration` 注解，可配合 `@ConditionalOnClass` / `@ConditionalOnProperty` 做条件加载
4. 需要暴露给非 Bean 环境的配置必须通过 Context 单例桥接

## 使用实例

### ✅ 正确示例

**目录结构：**
```
springboot-starter-mymodule/
  src/main/
    java/com/example/mymodule/
      AutoConfiguration.java
      MyModuleProperties.java
      MyModuleContext.java
    resources/META-INF/
      spring.factories
      spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
```

**spring.factories：**
```properties
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
com.example.mymodule.AutoConfiguration
```

**AutoConfiguration.imports：**
```
com.example.mymodule.AutoConfiguration
```

**AutoConfiguration.java：**
```java
@Configuration
public class AutoConfiguration implements InitializingBean {

    @Bean
    @ConfigurationProperties(prefix = "codingapi.mymodule")
    public MyModuleProperties myModuleProperties() {
        MyModuleProperties properties = new MyModuleProperties();
        MyModuleContext.getInstance().setProperties(properties);
        return properties;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 启动时的初始化逻辑
    }
}
```

### ❌ 错误示例

```properties
# 错误：仅有 spring.factories，缺少 AutoConfiguration.imports
# Spring Boot 3.x 环境下自动配置不会生效
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
com.example.mymodule.AutoConfiguration
```

```java
// 错误：直接使用 @Value 注入配置，非 Bean 环境无法获取
@Component
public class MyService {
    @Value("${codingapi.mymodule.timeout}")
    private int timeout;
}

// 错误：两个文件内容不一致
// spring.factories 列出了 A, B
// AutoConfiguration.imports 只列出了 A
// → 某些环境下 B 配置不生效，难以排查

// 错误：配置类未使用 @ConfigurationProperties
@Bean
public MyModuleProperties myModuleProperties() {
    return new MyModuleProperties();  // 属性不会被自动绑定
}
```

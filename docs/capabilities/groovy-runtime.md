---
name: groovy-runtime
description: Apache Groovy 运行时 — 动态脚本编译、执行与类型系统桥接
status: 已实现
scope: 后端
source: 框架:Groovy
import: org.apache.groovy:groovy
framework_version: 4.0.24
---

## 解决什么问题

Groovy 作为 JVM 上的动态语言，在本框架中被广泛使用：

- **工作流表达式**：FlowNode 的 `TitleGenerator`、`OperatorMatcher`、`OutTrigger` 使用 Groovy 脚本
- **动态脚本引擎**：`springboot-starter-script` 提供完整的 Groovy 脚本运行时
- **类型桥接**：Groovy 与 Java 之间的自动类型转换
- **JSON/XML 处理**：通过 `groovy-json` 和 `groovy-xml` 模块处理结构化数据

## 如何使用

### Groovy 脚本执行

```groovy
// Groovy 脚本可以直接使用 Java 类型
def greeting(String name) {
    return "Hello, ${name}!"
}
```

### GroovyShell 使用

```java
GroovyShell shell = new GroovyShell();
Script script = shell.parse("return a + b");
Binding binding = new Binding();
binding.setVariable("a", 10);
binding.setVariable("b", 20);
script.setBinding(binding);
Object result = script.run(); // 30
```

### 本框架中的使用场景

- **工作流引擎**：节点标题生成、审批人匹配、条件分支触发器
- **脚本引擎**：动态业务规则、表单校验、报表查询
- **数据查询**：FastRepository 中的动态 HQL 构建辅助

## 使用实例

```java
// 工作流中使用 Groovy 匹配审批人
OperatorMatcher matcher = new OperatorMatcher(
    "def matcher(session) { return [session.createOperatorId] }"
);
List<Long> operatorIds = matcher.matcher(flowSession);
```

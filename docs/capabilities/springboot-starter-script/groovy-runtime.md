---
name: springboot-starter-script/groovy-runtime
module: springboot-starter-script
description: Groovy 脚本运行时引擎，支持运行时编译、LRU 缓存、热更新和 REST API
status: 已实现
scope: 后端
source: 项目自有
import: "com.codingapi.springboot:springboot-starter-script"
symbols:
  - GroovyScriptRuntime
  - GroovyScript
  - GroovyScriptCacheContext
  - GroovyScriptRuntimeContext
  - GroovyScriptController
content_hash: deddb103b5a58509ca7c08ace4195760e618f5b07782cfcb857fc1326d4ae526
---

## 解决什么问题

在企业应用中，部分业务逻辑需要频繁调整但不希望每次都经历"改代码→编译→部署"的完整周期。典型场景包括：

- **动态规则计算**：促销折扣、费率计算、风控阈值等业务规则经常变化
- **自定义报表/导出**：不同租户或部门的报表格式差异大，用脚本比硬编码更灵活
- **流程条件表达式**：工作流节点的条件判断、操作者匹配等需要运行时求值
- **临时数据处理**：运维脚本、数据修复等一次性任务

`groovy-runtime` 提供了嵌入式的 Groovy 脚本执行引擎，具备以下特性：

- **运行时编译执行**：无需重启应用即可加载和执行新脚本
- **LRU 编译缓存**：基于 SHA256 指纹缓存已编译的 Script 对象，避免重复编译开销
- **三级存储**：内存缓存 → 临时存储 → 持久化仓库，兼顾性能和可靠性
- **事务集成**：支持只读（READONLY）和提交（COMMIT）两种事务模式
- **REST API**：内置 Controller 提供脚本编译、查询、保存接口，方便管理端对接

## 如何使用

### 核心组件

| 组件 | 职责 |
|------|------|
| `GroovyScriptRuntime` | 底层执行引擎：GroovyShell 封装、LRU 编译缓存、invoke/run 方法、事务控制 |
| `GroovyScriptRuntimeContext` | Runtime 的单例上下文，从配置读取 `shellMaxCacheSize` 初始化 |
| `GroovyScript` | 脚本领域对象：封装 key/script/method/returnType/binds 等元信息，提供 compile/run/invoke 快捷方法 |
| `GroovyScriptCacheContext` | 脚本对象的 LRU 缓存（最大 10240 条），三级查找：缓存 → 临时存储 → Repository |
| `GroovyScriptController` | REST 端点 `/api/groovy-script/*`，提供 compile/getScript/getMetadata/save 接口 |

### 直接执行脚本

通过 `GroovyScriptRuntimeContext` 单例直接运行脚本：

```java
GroovyScriptRuntimeContext ctx = GroovyScriptRuntimeContext.getInstance();

// 简单执行
String result = ctx.run("'hello ' + name", String.class,
    TransactionMode.DEFAULT, Map.of("name", "world"));

// 调用脚本中的函数
int sum = ctx.invoke("add", "def add(a,b){ a + b }", int.class,
    TransactionMode.DEFAULT, null, 3, 5);
```

### 使用 GroovyScript 对象

对于需要持久化和复用的脚本，使用 `GroovyScript` 封装：

```java
GroovyScript script = GroovyScript.builder("discount_calc")
    .script("""
        def calculate(price, rate) {
            return price * rate
        }
    """)
    .method("calculate")
    .returnType(BigDecimal.class)
    .description("折扣计算脚本")
    .tag("pricing")
    .build();

// 编译并缓存
script.compile(true);

// 执行
BigDecimal result = script.invoke(Map.of(), new BigDecimal("100"), new BigDecimal("0.85"));

// 持久化到仓库
script.save();
```

### 事务模式

`TransactionMode` 支持三种模式：

| 模式 | 行为 |
|------|------|
| `DEFAULT` | 不参与事务，由调用方自行控制 |
| `READONLY` | 在只读事务中执行，适合查询类脚本 |
| `COMMIT` | 在可提交事务中执行，适合写入类脚本 |

### REST API

内置的 `GroovyScriptController` 提供以下端点：

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/groovy-script/compile` | 编译脚本（body: `{script, cache}`) |
| GET | `/api/groovy-script/getScript?key=` | 获取脚本内容 |
| GET | `/api/groovy-script/getMetadata?key=` | 获取脚本元数据 |
| POST | `/api/groovy-script/save` | 保存脚本（编译+持久化） |

## 使用实例

以下示例展示一个完整的动态折扣计算场景：

```java
// 1. 创建并保存折扣计算脚本
GroovyScript discountScript = GroovyScript.builder("member_discount")
    .script("""
        def calcDiscount(amount, memberLevel) {
            switch(memberLevel) {
                case 'GOLD':   return amount * 0.8
                case 'SILVER': return amount * 0.9
                default:       return amount
            }
        }
    """)
    .method("calcDiscount")
    .returnType(BigDecimal.class)
    .description("会员折扣计算")
    .typeOne("pricing")
    .typeTwo("discount")
    .build();

// 编译并持久化
discountScript.compile(true);
discountScript.save();

// 2. 后续使用时直接从缓存获取
GroovyScript cached = GroovyScriptCacheContext.getInstance()
    .getGroovyScript("member_discount");

// 3. 执行业务计算
BigDecimal originalAmount = new BigDecimal("500");
BigDecimal finalPrice = cached.invoke(
    TransactionMode.READONLY, null, originalAmount, "GOLD");
// finalPrice = 400.0

// 4. 热更新：修改脚本后重新保存即可生效
cached.setScript("""
    def calcDiscount(amount, memberLevel) {
        switch(memberLevel) {
            case 'DIAMOND': return amount * 0.7
            case 'GOLD':    return amount * 0.8
            case 'SILVER':  return amount * 0.9
            default:        return amount
        }
    }
""");
cached.compile(true);
cached.save();
// 下次 invoke 自动使用新版本
```

缓存机制说明：`GroovyScriptRuntime` 内部以脚本内容的 SHA256 作为缓存 key，相同内容的脚本不会重复编译。当脚本内容变更后，SHA256 值改变，自动触发重新编译。LRU 策略确保内存占用可控，默认上限可通过配置项调整。

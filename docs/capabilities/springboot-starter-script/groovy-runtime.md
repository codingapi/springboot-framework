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
  - GroovyScriptCache
  - GroovyScriptCacheSync
  - GroovyScriptCacheSyncContext
  - GroovyScriptCacheEvent
  - LocalGroovyScriptCacheSync
  - GroovyScriptRuntimeContext
  - GroovyScriptController
content_hash: deddb103b5a58509ca7c08ace4195760e618f5b07782cfcb857fc1326d4ae526
---

## 解决什么问题

在企业应用中，部分业务逻辑需要频繁调整但不希望每次都经历"改代码→编译→部署"的完整周期。典型场景包括：

- **动态规则计算**：促销折扣、费率计算、风控阈值等业务规则经常变化
- **自定义报表/导出**：不同租户或部门的报表格式差异大，用脚本比硬编码更灵活
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
| `GroovyScriptCacheContext` | 脚本对象的 LRU 缓存（最大 10240 条），三级查找：缓存 → 临时存储 → Repository；缓存容器可通过 `setCache` 替换 |
| `GroovyScriptCache` / `DefaultGroovyScriptCache` | 缓存容器接口与默认进程内 LRU 实现，多实例部署时可替换为 Redis 等共享缓存 |
| `GroovyScriptCacheEvent` | 脚本缓存变更事件（SAVE / REMOVE），实现 `ISyncEvent`，可被进程内 `IHandler` 监听 |
| `GroovyScriptCacheSync` / `GroovyScriptCacheSyncContext` | 缓存变更跨实例同步 SPI 与上下文：写路径发布事件，远端实例接收后失效本地缓存 |
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

## 多实例部署（重要）

脚本对象缓存（`GroovyScriptCacheContext`）与临时脚本缓存都是**进程内**的。多实例部署（水平扩容、双端部署、多副本灰度）时，实例 A 修改脚本后，实例 B 的缓存不会自动感知——如果不做任何配置，B 将永久返回旧脚本（见 issue #214）。

### 部署前提

多实例部署**必须同时满足**以下两点，缺一不可：

1. **共享仓储**：通过 `GroovyScriptRepositoryContext.getInstance().setRepository(...)` 注入基于数据库（或 Redis）的仓储实现。模块默认的 `DefaultGroovyScriptRepository` 是进程内 HashMap，不提供跨实例数据共享；
2. **跨实例失效同步**：实现 `GroovyScriptCacheSync` SPI 广播缓存变更事件，远端实例收到事件后自动失效本地缓存，下次读取回源仓储加载新版本。

单实例部署无需任何配置，默认 `LocalGroovyScriptCacheSync` 保持原有行为。

### Redis pub/sub 失效同步实现示例（推荐方案）

数据正文保存在共享数据库中，Redis 仅承担"失效通知"的广播通道，失效后各实例自动回源数据库加载新版本。该方案实现简单、无需处理脚本内容的序列化兼容问题。

依赖：`spring-boot-starter-data-redis`（版本由 Spring Boot BOM 管理）+ `com.alibaba.fastjson2:fastjson2`。

```java
/**
 * 基于 Redis pub/sub 的脚本缓存跨实例同步实现
 * 广播通道：脚本保存/删除事件 -> 其他实例失效本地缓存并回源仓储
 */
@Component
public class RedisGroovyScriptCacheSync implements GroovyScriptCacheSync {

    public static final String CHANNEL = "groovy-script-cache-sync";

    private final StringRedisTemplate redisTemplate;

    public RedisGroovyScriptCacheSync(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void publish(GroovyScriptCacheEvent event) {
        // 事件包含 source（来源实例标识），远端实例 receive 时会自动过滤自身事件避免回环
        redisTemplate.convertAndSend(CHANNEL, JSON.toJSONString(event));
    }

    /**
     * 订阅回调：收到远端事件后失效本地缓存
     * 由下方 RedisMessageListenerContainer 配置调用
     */
    public void onMessage(String payload) {
        GroovyScriptCacheEvent event = JSON.parseObject(payload, GroovyScriptCacheEvent.class);
        // receive 内部：过滤本实例事件 -> 按事件类型 remove 本地缓存 key
        GroovyScriptCacheSyncContext.getInstance().receive(event);
    }
}
```

```java
/**
 * 注册 Redis 频道订阅
 */
@Configuration
public class GroovyScriptCacheSyncConfiguration {

    @Bean
    public RedisMessageListenerContainer groovyScriptCacheSyncListenerContainer(
            RedisConnectionFactory connectionFactory,
            RedisGroovyScriptCacheSync sync) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener((message, pattern) -> {
            String payload = new String(message.getBody(), StandardCharsets.UTF_8);
            sync.onMessage(payload);
        }, new ChannelTopic(RedisGroovyScriptCacheSync.CHANNEL));
        return container;
    }
}
```

> 说明：若事件序列化使用 JSON，`GroovyScriptCacheEvent` 的字段（type/key/source/updateTime）均为可序列化类型；`GroovyScriptCacheEvent` 同时实现了框架的 `ISyncEvent`，进程内业务可通过 `IHandler<GroovyScriptCacheEvent>` 监听脚本变更，与跨实例同步互不影响。

### Redis 共享缓存容器实现示例（可选）

如果希望连脚本对象的读取也走 Redis（例如仓储回源成本高的场景），可以实现 `GroovyScriptCache` 接口替换默认 LRU 容器：

```java
/**
 * 基于 Redis 的脚本缓存容器
 * 注意：GroovyScript 含 Class<?> 类型字段（returnType/binds/requests），
 * 序列化时需记录类名、反序列化时按类名反射加载，此处仅示意骨架
 */
public class RedisGroovyScriptCache implements GroovyScriptCache {

    private final StringRedisTemplate redisTemplate;
    private final Duration ttl = Duration.ofMinutes(30);

    public RedisGroovyScriptCache(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private String cacheKey(String key) {
        return "groovy-script:cache:" + key;
    }

    @Override
    public void save(GroovyScript script) {
        if (script != null) {
            redisTemplate.opsForValue().set(cacheKey(script.getKey()), serialize(script), ttl);
        }
    }

    @Override
    public GroovyScript get(String key) {
        String payload = redisTemplate.opsForValue().get(cacheKey(key));
        return payload == null ? null : deserialize(payload);
    }

    @Override
    public void remove(String key) {
        redisTemplate.delete(cacheKey(key));
    }

    @Override
    public List<String> keys() {
        // 生产环境建议使用 SCAN 遍历，避免 KEYS 阻塞
        return redisTemplate.keys("groovy-script:cache:*").stream()
                .map(k -> k.substring("groovy-script:cache:".length()))
                .toList();
    }

    @Override
    public int count() {
        return keys().size();
    }

    @Override
    public void clear() {
        keys().forEach(redisTemplate::delete);
    }

    // serialize/deserialize 需处理 Class<?> 字段（记录类名 -> 反射加载），此处从略
}
```

```java
// 启用时替换默认缓存容器（通常在配置类中执行一次）
GroovyScriptCacheContext.getInstance().setCache(new RedisGroovyScriptCache(redisTemplate));
```

**方案选择建议**：

| 方案 | 适用场景 | 复杂度 |
|------|---------|--------|
| Redis pub/sub 失效通知（推荐） | 脚本正文在共享数据库中，改动频率低、读取频率高 | 低 |
| Redis 共享缓存容器 + pub/sub | 仓储回源成本高，或数据库无法承载读取压力 | 中（需处理 `Class<?>` 字段序列化） |

### 行为说明与注意事项

- 收到远端 `SAVE`/`REMOVE` 事件后，本实例仅失效对应 key 的缓存，**下次读取自动回源仓储**，无需主动拉取脚本内容；
- 已编译 Script 的运行时缓存（`GroovyScriptRuntime`）以脚本内容 SHA256 为 key，内容变化后自动使用新 key 编译，无需额外失效；
- `GroovyScriptCacheEvent.source` 为各实例的 UUID 标识（`GroovyScriptCacheSyncContext.getId()`），`receive` 会忽略本实例的事件，避免广播回环；
- 临时脚本（`temp()`）与默认临时仓储均为进程内设计，临时编辑结果对其他实例不可见，属于预期行为；
- 默认 LRU 容器为进程内实现，容量上限 10240 条；多实例下即使不替换为 Redis 缓存容器，只要接入了失效同步，各实例也会在收到事件后回源加载最新脚本。

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

缓存机制说明：`GroovyScriptRuntime` 内部以脚本内容的 SHA256 作为缓存 key，相同内容的脚本不会重复编译。当脚本内容变更后，SHA256 值改变，自动触发重新编译。LRU 策略确保内存占用可控，默认上限可通过配置项调整。脚本对象缓存的跨实例失效机制见上文[多实例部署](#多实例部署重要)章节（issue #214）。

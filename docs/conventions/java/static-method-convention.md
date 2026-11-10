---
name: java/static-method-convention
module: java
description: 静态方法规范：领域判断回归实例方法，明确允许的 static 场景
status: 已实现
scope: 后端
source: 项目自有
---

## 解决什么问题

静态函数「放哪都行」——谁都能调、谁都能再写一份——是逻辑失去归属的典型信号：

- 明明是「这个对象和那个对象是不是同一个」的判断，却写成 `XxxUtils.isSameAbc(a, b)`，于是同一套规则在项目里出现第二份、第三份，口径逐渐分叉；
- 因为不需要拥有对象就能调用，静态方法成为「不想想这段逻辑归谁」时的默认落点；
- 逻辑散落后，改规则要全局搜索，漏改一处就是线上不一致。

## 如何使用

### 判据

**如果这个方法的第一个参数是「某个对象」，那它就该是那个对象的实例方法。**

- ❌ 承载单个对象的领域判断 → 必须回到对象上做实例方法；
- 曾经被判定违规的例子：`isSamePlan`、`isSameType`、`isSameIndicator`、`containsType`、`contains`、`containsIndicator`、`containsRater`——它们全部应改为对象上的 `isSameAs` / `isSameAsAny`。

### 允许的 static 场景

| 场景 | 示例 | 说明 |
|------|------|------|
| 常量 | `STATE_END`、`HUNDRED`、`CREATE_TIME_TOLERANCE` | 无行为，只有值 |
| 值对象的静态工厂 | `Money.of(...)`、`Response.buildSuccess()` | 创建本身是类型职责 |
| 内部静态类 | `Content`、`AttachmentItem` | 从属结构，不是独立领域概念 |
| 作用在集合整体上的规则执行 | `ValidationResult.throwIfFailed(List)` | 规则对象是「这批结果的汇总」，不隶属于其中单个元素 |

判断口诀同上：第一个参数是单个对象就回到对象上；第一个参数是**集合整体**且规则本身就是「对一批数据做汇总/执行」时，静态方法才成立。

### 补充：可变状态不要做成静态单例

注册表、缓存、上下文这类有状态对象，即便能力本身合法，也不应做成 `static` 单例。全局可变状态会让测试互相污染、并发不可控。正确形态是**由装配层创建并持有实例**，通过构造注入使用。

## 使用实例

### ✅ 正确示例

```java
// 值对象的静态工厂：创建是类型职责
public final class Money {
    private final BigDecimal amount;

    private Money(BigDecimal amount) {
        this.amount = amount;
    }

    public static Money of(String amount) {
        return new Money(new BigDecimal(amount));
    }
}
```

```java
// 作用在集合整体上的规则执行
public final class ValidationResult {

    public static void throwIfFailed(List<ValidationResult> results) {
        List<String> messages = results.stream()
                .filter(ValidationResult::isFailed)
                .map(ValidationResult::getMessage)
                .collect(Collectors.toList());
        if (!messages.isEmpty()) {
            throw new BusinessException(String.join("；", messages));
        }
    }
}
```

```java
// 同一性判断回到对象上
project.isSameAs(other);
project.isSameAsAny(projects);
```

### ❌ 错误示例

```java
// ❌ 第一个参数是对象，却写成静态工具方法
public final class ProjectUtils {
    public static boolean isSameProject(Project a, Project b) { ... }
    public static boolean containsProject(List<Project> list, Project p) { ... }
}
```

```java
// ❌ 为「方便」新建工具类，把领域规则搬出模型
public final class RateeUtils {
    public static boolean containsRater(List<RateeRaterWeight> weights, Rater rater) { ... }
}
```

```java
// ❌ 有状态对象做成静态单例
public final class StrategyRegistry {
    private static final StrategyRegistry INSTANCE = new StrategyRegistry();
    private final Map<String, Strategy> strategies = new HashMap<>();

    public static StrategyRegistry getInstance() {
        return INSTANCE;
    }
}
```

## 自查清单

- [ ] 新增的 static 方法，第一个参数是否其实是「某个对象」
- [ ] 同一性 / 包含 / 是否某类型等判断是否已回到对象实例方法
- [ ] 剩余 static 是否只落在常量、静态工厂、内部静态类、集合整体规则四类
- [ ] 有状态对象是否由装配层持有实例，而非静态单例

---
name: java/test-design-convention
module: java
description: 测试设计规范：模型不为测试让路，区分模型能力与测试脚手架
status: 已实现
scope: 后端
source: 项目自有
---

## 解决什么问题

为了让单元测试跑通而改模型，是本项目最容易犯、也最难回退的错误：

- 给模型加注入的时钟、跳过校验的开关、`@VisibleForTesting` 方法、仅为断言准备的 getter，测试绿了，业务模型却多了一堆生产环境永远不会走的分支；
- 「只被测试调用」的代码被误判为脚手架删掉，结果删掉的是本该存在的建模能力（如策略注册表的注册、清空、重新装配），留下功能缺口；
- 两种错误方向相反，但根因相同：**用「谁在调用」而不是「能力本身是否成立」来判断代码该不该存在**。

## 如何使用

### 铁律：模型不为测试让路

**不能为了单元测试去调整模型的能力、结构或代码路径。**

改动模型的唯一合法理由是「模型自身的业务控制逻辑有缺陷」——那时改的是缺陷，不是为了测试。测试跑不通，不是改模型的理由。

### 划界判据

判据不是「现在有没有调用方」，而是「**这个能力离开测试是否仍然成立**」。

| 维度 | 模型能力（该有） | 测试脚手架（不该有） |
|------|------------------|----------------------|
| 形态 | 注册 / 清空 / 重置 / 启用禁用 / 状态推进 / 业务查询 | 注入时钟、跳过校验的开关、`@VisibleForTesting`、只为断言加的取值方法、为摆状态加的 setter、把 `private` 改成 `public` |
| 判据 | 业务语义上独立成立：运行期换策略、卡丢了重发、码用过作废、状态该推进 | 只在测试里成立：生产不会有第二个「现在几点」，不会有「跳过校验」的入口 |
| 后果 | 删了就是功能缺失 | 留着就是把测试的方便写进了业务模型 |

> 反例说明：`addStrategy` / `clear()` 在主代码里零调用、只有测试装配时调用，但它们**不违规**——策略注册表本来就该能注册、能清空、能重新装配，只是装配层尚未落地。这类能力属于建模缺口，装配层建立后应补上生产侧接线，而不是删除。

### 测试脚手架的正确替代路径（按优先级）

| 优先级 | 测试诉求 | 正确做法 |
|--------|----------|----------|
| a | 业务需要知道某个信息 | 作为**输入参数**传入（如提交时刻、活动时间窗），由调用方决定值 |
| b | 业务只需记录「何时发生」 | 由对象自取系统时间（副作用本就该由对象做） |
| c | 测试想控制「现在几点」 | 测试诉求，不进模型：用 `isBetween(before, after)` 区间断言，或替换端口实现 |
| d | 测试要读内部才能断言 | 先问业务是否需要这个读能力；需要就作为正当查询保留，不需要就断言行为结果（状态、异常） |
| e | 测试「摆不出状态」 | 缺的是表达能力或装配入口：补模型能力或补装配，不加测试后门 |

### 时钟为什么违规

被诟病的不是 `Clock` 这个类型，而是模型为了测试多了一个字段和重载构造，让外部能改变它的行为。「现在几点」在生产侧没有第二种答案，这个能力只在测试里成立。

正确做法：模型里不保留时钟字段、重载构造与可变时钟实现，改为两条路径——**时间作为输入**、**创建时间自取**。

## 使用实例

### ✅ 正确示例

```java
// 需要判断「此刻是否在活动期内」→ 时间作为输入
public class Evaluation {
    private final Instant startTime;
    private final Instant endTime;

    public boolean isInProcess(Instant now) {
        return !now.isBefore(startTime) && now.isBefore(endTime);
    }
}
```

```java
// 只需记录「何时提交」→ 对象自取系统时间
public class AnswerSheet {
    private Instant submittedAt;

    public void submit() {
        this.submittedAt = Instant.now();
    }
}
```

```java
// 测试：区间断言，不改模型
Instant before = Instant.now();
sheet.submit();
Instant after = Instant.now();
assertTrue(sheet.getSubmittedAt().isAfter(before.minusSeconds(1)));
assertTrue(sheet.getSubmittedAt().isBefore(after.plusSeconds(1)));
```

### ❌ 错误示例

```java
// ❌ 为测试注入时钟：模型多了一个只有测试会用到的字段与构造
public class AnswerSheet {
    private final Clock clock;

    public AnswerSheet() {
        this(Clock.systemDefaultZone());
    }

    @VisibleForTesting
    public AnswerSheet(Clock clock) {
        this.clock = clock;
    }

    public void submit() {
        this.submittedAt = clock.instant();
    }
}
```

```java
// ❌ 为测试加跳过校验的开关
public void submit(boolean skipValidation) {
    if (!skipValidation) {
        requireAnswerComplete();
    }
    this.submittedAt = Instant.now();
}
```

```java
// ❌ 把 private 改成 public / 加只为断言的 getter
public List<Item> items;                     // 只为测试摆状态
public String internalCacheKey() { ... }     // 只为测试断言中间值
```

## 自查清单

- [ ] 本次改动是否为了让测试通过而修改了模型（字段、构造、可见性、分支）
- [ ] 新增的能力离开测试是否仍然成立
- [ ] 是否可以用「输入参数 / 区间断言 / 行为断言」替代测试后门
- [ ] 被删除的「零调用」代码，是否其实是尚未接线的建模能力

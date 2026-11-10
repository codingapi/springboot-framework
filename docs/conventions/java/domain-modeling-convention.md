---
name: java/domain-modeling-convention
module: java
description: 领域建模规范：同一性判断、聚合间深复制、可配置单一出口与只读集合
status: 已实现
scope: 后端
source: 项目自有
---

## 解决什么问题

领域逻辑写成「外部函数 + 到处开的口子」后，模型只剩数据容器：

- 同一性判断写成静态函数，散落在各个 Service 里，口径逐渐分叉；
- 覆盖 `equals` 用 `id` 比较，但实体 `id` 会从 0 变为非 0，放进 `HashSet` / `HashMap` 后取不出来；
- 聚合之间直接共享可变引用，改一个项目的名单，方案模板跟着变，下一次立项少人；
- 「还能不能改」各处各写一遍 `state != END`，理解不一致；
- 集合直接从 getter 外泄，所有「不许改」的校验被一句 `getXxx().add(...)` 绕过去。

## 如何使用

### 同一性判断属于对象

统一使用实例方法 `isSameAs`，规则固定为：**同引用，或同类且 `id` 非 0 并相等**。

- 不覆盖 `equals`：实体 `id` 会从 0 变为非 0，破坏哈希容器不变量；
- 批量版本 `isSameAsAny(Collection)` 也属于对象，读作「我是否在这组里」；
- 禁止写成 `containsXxx(集合, 我)` 这类外部静态函数。

### 重载歧义换名字，不靠强转

当一个类型同时实现两个都声明了 `isSameAs` 的接口时（如部门既是组织、又是参与方），「拿部门比部门」会让编译器无法选择。此时**换名字**而不是强转，例如组织侧叫 `isSameOrganizationAs`。「判断同一性的是这个对象」这条原则不变，只是名字要让编译器分得清。

### 聚合之间复制，不共享可变引用

跨聚合复制时**深复制行与值**，定义类对象（指标定义、类型定义等）仍可共用。

判断口诀：**A 的编辑动作会不会写进 B 的资料？会，就说明该复制。**

### 「能不能改」只判定一次

聚合根暴露唯一的 `isConfigurable()`，所有调整动作先过 `requireConfigurable(action)`，提示语带上动作名。禁止各处自写 `state != END`，各自理解什么叫「还能改」。

### 集合出口返回只读快照

`getXxx()` 返回 `List.copyOf(...)`，要改只能走领域动作。只包一层外层列表不够，**内层对象自己的集合也要收口**。

## 使用实例

### ✅ 正确示例

```java
public class Project {
    private long id;

    public boolean isSameAs(Project other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        return id != 0 && id == other.id;
    }

    public boolean isSameAsAny(Collection<Project> others) {
        return others.stream().anyMatch(this::isSameAs);
    }
}
```

```java
// 深复制行与值，定义对象共用
public Project copyFrom(PlanInstance plan) {
    Project project = new Project();
    project.items = plan.getItems().stream()
            .map(PlanItem::copy)
            .collect(Collectors.toList());
    project.weight = plan.getWeight();
    project.indicator = plan.getIndicator();   // 定义对象共用
    return project;
}
```

```java
// 唯一出口 + 带动作名的提示
public boolean isConfigurable() {
    return state != State.END;
}

private void requireConfigurable(String action) {
    if (!isConfigurable()) {
        throw new BusinessException("当前状态不允许" + action);
    }
}

public void adjustWeight(int weight) {
    requireConfigurable("调整权重");
    this.weight = weight;
}
```

```java
// 只读快照
public List<PlanItem> getItems() {
    return List.copyOf(items);
}
```

### ❌ 错误示例

```java
// ❌ 覆盖 equals 用可变 id，破坏哈希容器
@Override
public boolean equals(Object o) {
    return o instanceof Project && ((Project) o).id == this.id;
}
```

```java
// ❌ 外部静态函数判断同一性，口径分散
public static boolean isSamePlan(Project a, Project b) { ... }
public static boolean containsProject(List<Project> list, Project p) { ... }
```

```java
// ❌ 共享可变引用：改项目会写回模板
public Project from(PlanInstance plan) {
    Project project = new Project();
    project.items = plan.getItems();   // 同一个 List
    return project;
}
```

```java
// ❌ 「能不能改」各处自行理解
public void adjustWeight(int weight) {
    if (state == State.END) {          // 别处可能还允许 PAUSED
        throw new BusinessException("已结束");
    }
    this.weight = weight;
}
```

```java
// ❌ 集合直接外泄
public List<PlanItem> getItems() {
    return items;                       // 调用方可直接 add / remove
}
```

## 自查清单

- [ ] 同一性判断是否为对象上的 `isSameAs` / `isSameAsAny`
- [ ] 是否没有覆盖 `equals`（实体/聚合根）
- [ ] 跨聚合复制是否深复制了行与值
- [ ] 「能不能改」是否只经 `isConfigurable()` + `requireConfigurable(action)`
- [ ] 集合出口是否为只读快照，内层集合是否也收口

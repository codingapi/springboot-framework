---
name: java/naming-convention
module: java
description: 方法命名规范：按返回数量选择 get/find，必须存在的场景用 require
status: 已实现
scope: 后端
source: 项目自有
---

## 解决什么问题

方法名不表达「返回几条」和「是否保证存在」，调用方只能靠猜：

- 名字叫 `getXxx` 却可能返回 `null`，调用方默认非空、不做判空，最终在业务路径上 NPE，把本该给出的业务提示（如「访问码不存在或已失效」）顶掉；
- 同一个语义在不同类里分别叫 `get` / `find` / `query` / `load`，阅读和检索成本高；
- 「必须存在」的校验散落在调用方，提示语各处一份、口径不一；
- 为了「保证不为空」，在 `get` 里偷偷抛业务异常，名字与行为不符，调用方无从预判。

## 如何使用

### 核心规则

1. **按返回数量选动词，不看是否可能为空**：
   - 返回一条数据 → `getXxx`（返回对象或 `Optional` 都叫 `get`）；
   - 返回多条数据 → `findXxx`（返回 `List`、`Set` 等集合）。
2. **「必须存在，否则报错」→ 用语义动词表达前提**：使用 `requireXxx`（或 `loadXxxOrThrow` 这类明确的词），把前提写进方法名，不要靠 `get` 里偷偷抛异常。
3. **`get` 是纯查询**：不校验、不抛业务异常。「找不到」由返回值表达——对象可为 `null`，或返回 `Optional`。
4. **「必须有」的场合由调用方决定处理方式**：`orElseThrow`，或包一层 `requireXxx` 把提示语集中在一处。
5. **命名描述契约，不描述实现**：`get` / `find` 只用于查询语义；有副作用或写入行为的操作不得使用 `get` / `find` 前缀，改用能表达动作的动词（`create`、`save`、`remove` 等）。

### 命名对照

| 语义 | 命名 | 返回约定 | 不存在时 |
|------|------|----------|----------|
| 取单条（可能不存在） | `getXxx` | 对象或 `Optional` | 返回 `null` / `Optional.empty()` |
| 取多条 | `findXxx` | `List` / `Set` | 返回空集合，不返回 `null` |
| 取单条（必须存在） | `requireXxx` | 对象 | 抛业务异常 |
| 判断存在 | `existsXxx` | `boolean` | — |

## 使用实例

### ✅ 正确示例

```java
public class AccountService {

    // 单条：可能为 null
    public Account getAccount(long id) {
        return accountRepository.findById(id);
    }

    // 单条：用 Optional 表达可能不存在
    public Optional<Account> getAccountByNo(String accountNo) {
        return accountRepository.findByNo(accountNo);
    }

    // 多条：find
    public List<Account> findAccountsByOwner(long ownerId) {
        return accountRepository.findAllByOwner(ownerId);
    }

    // 必须存在：前提写在名字里，提示语集中在一处
    public Account requireAccount(long id) {
        Account account = getAccount(id);
        if (account == null) {
            throw new BusinessException("账户不存在：" + id);
        }
        return account;
    }
}
```

```java
// 调用方：需要「必须存在」时显式处理
Account account = accountService.requireAccount(accountId);
// 或
Account account = accountService.getAccountByNo(no)
        .orElseThrow(() -> new BusinessException("账户不存在：" + no));
```

### ❌ 错误示例

```java
// ❌ get 返回 null，名字却暗示「已拿到有效对象」
private AccessCode getActiveCode(String scene) {
    AccessCode code = accessCodeRepository.findByScene(scene);
    if (code == null || code.isUsed()) {
        return null;
    }
    return code;
}

public void submitCode(String scene) {
    AccessCode code = getActiveCode(scene);
    code.markUsed();   // NPE：把「访问码不存在或已失效」的业务提示顶掉了
}
```

```java
// ❌ get 里做校验并抛业务异常：名字与行为不符
public Account getAccount(long id) {
    Account account = accountRepository.findById(id);
    if (account == null) {
        throw new BusinessException("账户不存在");   // 应叫 requireAccount
    }
    return account;
}
```

```java
// ❌ 返回多条却用 get
public List<Account> getAccounts(long ownerId) { ... }   // 应为 findAccounts
```

## 自查清单

- [ ] 返回单条的方法是否都叫 `getXxx`（`Optional` 也算 `get`）
- [ ] 返回多条的方法是否都叫 `findXxx`
- [ ] `get` / `find` 内是否没有任何业务校验与业务异常
- [ ] 「必须存在」的场景是否用 `requireXxx` 或调用方 `orElseThrow` 显式表达

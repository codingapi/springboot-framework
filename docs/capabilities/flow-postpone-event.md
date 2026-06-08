---
name: flow-postpone-event
description: 延期流程操作的事件通知 — 当前 FlowPostponedService 未发送 FlowApprovalEvent
status: 计划中
scope: 后端
source: 计划
---

## 解决什么问题

当前工作流引擎的所有操作（发起、提交、驳回、撤回、删除、作废、退回、完成、转办、催办、终止）都通过 `EventPusher` 发送 `FlowApprovalEvent`，业务侧可通过 `IHandler<FlowApprovalEvent>` 监听并联动处理。

**但延期（Postpone）操作是唯一的例外**：`FlowPostponedService.postponed()` 仅更新了 `FlowRecord` 的截止时间，没有发送任何事件。这导致：

- 业务系统无法感知延期操作，无法触发提醒、日志、通知等联动
- 与其他流程操作的契约不一致 — Handler 中 `isXxx()` 判断覆盖了所有操作，唯独缺少 `isPostponed()`
- 审计日志中延期操作无事件记录

## 如何使用

### 计划实现

1. 在 `FlowApprovalEvent` 中新增状态常量：
   ```java
   public static final int STATE_POSTPONED = 15;
   ```

2. 新增便捷判断方法：
   ```java
   public boolean isPostponed() {
       return state == STATE_POSTPONED;
   }
   ```

3. 在 `FlowPostponedService.postponed()` 末尾发送事件：
   ```java
   EventPusher.push(new FlowApprovalEvent(
       FlowApprovalEvent.STATE_POSTPONED,
       flowRecord, currentOperator, flowWork, bindData
   ));
   ```

4. 在 `event.md` 规范文档中将"延期流程"事件更新为 `POSTPONED`

### 业务监听

```java
@Component
public class LeavePostponedHandler implements IHandler<FlowApprovalEvent> {
    @Override
    public void handler(FlowApprovalEvent event) {
        if (event.isPostponed()) {
            FlowRecord record = event.getFlowRecord();
            // 发送延期提醒通知给申请人
            notifyService.sendDelayNotice(
                record.getCreateOperatorId(),
                record.getPostponedTime()
            );
        }
    }
}
```

## 使用实例

```java
// 当前状态（无事件）：
flowPostponedService.postponed(recordId, operator, delayMillis);
// → FlowRecord 截止时间已更新
// → ❌ 无 FlowApprovalEvent 发出

// 实现后（有事件）：
flowPostponedService.postponed(recordId, operator, delayMillis);
// → FlowRecord 截止时间已更新
// → ✅ FlowApprovalEvent(STATE_POSTPONED) 通过 EventPusher 发出
// → ✅ LeavePostponedHandler.handler() 被调用
```

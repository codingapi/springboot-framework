---
name: springboot-starter-flow/workflow-engine
module: springboot-starter-flow
description: 工作流引擎，支持流程定义、节点流转、审批、委托、会签和数据快照
status: 已实现
scope: 后端
source: 项目自有
import: "com.codingapi.springboot:springboot-starter-flow"
symbols:
  - FlowWork
  - FlowNode
  - FlowNodeService
  - FlowWorkBuilder
  - FlowStartService
  - FlowApprovalEvent
content_hash: 0deb7127074232e52335663402b08ead2e42469e6813e555a3dc06e97362fd04
---

## 解决什么问题

在企业级应用中，审批流程（如请假、报销、采购等）是高频且复杂的业务场景。传统硬编码方式存在以下痛点：

- **流程变更成本高**：每次调整审批节点或流转规则都需要修改代码并重新部署
- **审批模式单一**：难以同时支持会签、或签、传阅、退回、委托等多种审批形态
- **状态追踪困难**：流程实例的当前节点、历史审批记录、数据快照缺乏统一管理
- **事件通知分散**：审批通过、拒绝、转办等状态变更需要在多处手动触发通知逻辑

`workflow-engine` 提供了一套轻量级的嵌入式工作流引擎，将流程定义与业务代码解耦。通过 Builder 模式声明式构建流程，引擎自动处理节点流转、操作者匹配、数据快照绑定和事件推送，让开发者专注于业务逻辑本身。

## 如何使用

### 核心概念

| 概念 | 类 | 说明 |
|------|-----|------|
| 流程定义 | `FlowWork` | 描述一个完整的审批流程，包含节点集合、关系集合、启用状态等 |
| 流程节点 | `FlowNode` | 流程中的单个环节（开始/审批/传阅/结束），配置审批类型、操作者匹配器、超时时间等 |
| 节点关系 | `FlowRelation` | 定义节点之间的流转规则，支持条件触发（`OutTrigger`）和退回标记 |
| 流程构建器 | `FlowWorkBuilder` | 链式 API 构建 `FlowWork`，内部自动校验节点和关系的完整性 |
| 发起服务 | `FlowStartService` | 发起新流程实例，创建流程备份、数据快照和首条待办记录 |
| 节点服务 | `FlowNodeService` | 驱动节点流转：加载下一节点、匹配操作者、创建审批记录、处理传阅跳过 |
| 审批事件 | `FlowApprovalEvent` | 同步事件，覆盖创建/待办/通过/拒绝/转办/撤回/完成/催办/抄送/退回等 14 种状态 |
| 流程操作者 | `IFlowOperator` | 用户接口，支持委托 (`entrustOperator()`) 和管理员强制干预 |

### 构建流程

使用 `FlowWorkBuilder` 声明式定义流程：

```java
FlowWork work = FlowWorkBuilder.builder(operator)
    .title("请假审批流程")
    .description("员工请假审批")
    .skipIfSameApprover(true)       // 相同审批人自动跳过
    .postponedMax(3)                // 最大延期次数
    .nodes()
        .node("发起", "start", "startView", ApprovalType.UN_SIGN, startMatcher)
        .node("部门审批", "dept_approve", "approveView", ApprovalType.SIGN, deptMatcher, true, false)
        .node("HR备案", "hr_record", "hrView", ApprovalType.UN_SIGN, hrMatcher)
        .node("结束", "over", "overView", ApprovalType.UN_SIGN, endMatcher)
        .relations()
            .relation("发起->部门审批", "start", "dept_approve")
            .relation("部门审批->HR备案", "dept_approve", "hr_record")
            .relation("HR备案->结束", "hr_record", "over")
    .build();
```

构建时 `build()` 会自动调用 `enable()` → `verify()`，校验以下内容：
- 必须存在 `start` 和 `over` 节点及其关联关系
- 节点 code 不能重复
- 每个节点的 `titleGenerator` 和 `operatorMatcher` 不能为空

### 发起流程

通过 `FlowStartService` 发起流程实例：

```java
FlowStartService startService = new FlowStartService(
    workCode, operator, bindData, advice, repositoryHolder);
FlowResult result = startService.startFlow();
```

启动过程依次执行：加载并校验流程定义 → 创建版本快照（`FlowBackup`）→ 保存流程实例 → 序列化绑定数据 → 从 start 节点创建待办记录 → 推送 `FlowApprovalEvent`。

### 监听审批事件

实现 `IHandler<FlowApprovalEvent>` 即可接收所有审批状态变更：

```java
@Component
public class LeaveApprovalHandler implements IHandler<FlowApprovalEvent> {
    @Override
    public void handle(FlowApprovalEvent event) {
        if (event.isTodo()) {
            // 发送待办通知
        }
        if (event.isPass()) {
            // 审批通过处理
        }
        if (event.isFinish()) {
            // 流程结束归档
        }
    }
}
```

事件通过框架的 `EventPusher` 同步推送，在同一个事务内完成。

## 使用实例

以下示例展示一个完整的请假审批流程定义与发起：

```java
// 1. 定义操作者匹配器
OperatorMatcher startMatcher = OperatorMatcher.any();           // 发起人
OperatorMatcher deptMatcher = OperatorMatcher.script(          // 部门负责人
    "session.bindData.deptLeaderId");
OperatorMatcher hrMatcher = OperatorMatcher.script(            // HR
    "session.bindData.hrUserId");
OperatorMatcher endMatcher = OperatorMatcher.any();

// 2. 构建流程
FlowWork leaveFlow = FlowWorkBuilder.builder(currentUser)
    .title("请假审批")
    .skipIfSameApprover(true)
    .nodes()
        .node("发起申请", "start", "leaveStart", ApprovalType.UN_SIGN, startMatcher)
        .node("部门审批", "dept", "leaveApprove", ApprovalType.SIGN, deptMatcher, true, false)
        .node("HR确认", "hr", "leaveHr", ApprovalType.UN_SIGN, hrMatcher)
        .node("结束", "over", "leaveEnd", ApprovalType.UN_SIGN, endMatcher)
        .relations()
            .relation("提交", "start", "dept")
            .relation("批准", "dept", "hr")
            .relation("确认", "hr", "over")
    .build();

// 3. 准备业务数据
LeaveRequest leave = new LeaveRequest();
leave.setUserId(currentUser.getUserId());
leave.setDays(3);
leave.setDeptLeaderId(deptLeaderId);
leave.setHrUserId(hrUserId);

// 4. 发起流程
FlowStartService service = new FlowStartService(
    leaveFlow.getCode(), currentUser, leave, "申请年假3天", repoHolder);
FlowResult result = service.startFlow();

// 5. 获取待办记录
List<FlowRecord> records = result.getRecords();
records.forEach(r -> System.out.println(
    "待办: " + r.getTitle() + " -> " + r.getCurrentOperator().getName()));
```

当部门审批人点击通过后，引擎自动流转至 HR 确认节点；若设置了退回关系，审批人可选择退回至指定节点。全程数据快照通过 `BindDataSnapshot` 持久化，确保审批过程中业务数据不可变。

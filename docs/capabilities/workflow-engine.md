---
name: workflow-engine
description: 内置工作流引擎，支持流程定义、节点流转、审批、退回、会签、抄送、委托与数据快照
status: 已实现
scope: 后端
source: 项目自有
---

## 解决什么问题

企业应用中审批流程是核心业务场景，但引入外部工作流引擎（如 Activiti、Camunda）往往带来过重的依赖和复杂的 BPMN 学习成本。许多中小规模审批场景只需要一个轻量、可编程、与 DDD 架构自然融合的流程引擎。

`springboot-starter-flow` 提供了一个自研的轻量级工作流引擎，具备以下特点：

- **Schema 驱动**：流程定义通过 JSON Schema 描述，支持可视化编辑器生成，运行时由 `SchemaReader` 解析为 `FlowNode` + `FlowRelation` 对象图
- **完整的审批语义**：支持通过、退回、转办、委托、会签、抄送、催办、撤销、作废、停止等操作
- **数据快照**：流程发起时对绑定数据进行快照（`BindDataSnapshot`），确保审批过程中数据一致性
- **版本管理**：通过 `FlowBackup` 对流程定义进行版本快照，运行中的流程始终使用发起时的版本
- **事件驱动**：每个状态变更推送 `FlowApprovalEvent`（同步事件），业务层通过 `IHandler<FlowApprovalEvent>` 响应
- **脚本化扩展**：节点的标题生成（`TitleGenerator`）、操作者匹配（`OperatorMatcher`）、异常触发（`ErrTrigger`）均支持脚本表达式

## 如何使用

### 1. 引入依赖

```xml
<dependency>
    <groupId>com.codingapi.springboot</groupId>
    <artifactId>springboot-starter-flow</artifactId>
</dependency>
```

### 2. 实现 Repository 接口

框架定义了以下仓储接口，需由基础设施层提供持久化实现：

| 接口 | 职责 |
|------|------|
| `FlowWorkRepository` | 流程定义的增删改查 |
| `FlowRecordRepository` | 审批记录的保存与查询 |
| `FlowBackupRepository` | 流程版本快照的存储与恢复 |
| `FlowProcessRepository` | 流程实例的管理 |
| `FlowOperatorRepository` | 流程参与者的查询（`findByIds`） |
| `FlowBindDataRepository` | 绑定数据快照的存储 |

### 3. 实现 IFlowOperator 接口

所有流程参与者需实现 `IFlowOperator` 接口：

```java
public interface IFlowOperator {
    long getUserId();          // 用户ID
    String getName();          // 用户名称
    boolean isFlowManager();   // 是否流程管理员（可强制干预流程）
    IFlowOperator entrustOperator(); // 委托操作者（不为空时由委托人执行）
}
```

### 4. 设计流程

通过 `FlowWork` 构建流程定义：

```java
FlowWork flowWork = new FlowWork(createUser);
flowWork.setTitle("请假审批");
flowWork.setCode("leave_approval");
flowWork.setDescription("员工请假审批流程");

// 通过 Schema 解析节点和关系
flowWork.schema(schemaJson);

// 验证并启用
flowWork.verify();
flowWork.enable();
```

核心领域对象：
- **`FlowWork`**：流程定义，包含节点列表（`nodes`）和关系列表（`relations`），支持 `copy()` 复制、`schema()` 解析、`verify()` 校验
- **`FlowNode`**：流程节点，具有 `code`、`type`（NodeType）、`approvalType`（ApprovalType）、`titleGenerator`、`operatorMatcher`、`errTrigger`、`buttons`、`timeout` 等属性
- **`FlowRelation`**：节点间关系，定义源节点到目标节点的流转规则，支持条件触发（`OutTrigger`）和回退标记（`isBack`）
- **`FlowRecord`**：审批记录，记录每个节点的审批状态、操作者、意见等

### 5. 发起流程

使用 `FlowStartService` 发起流程：

```java
FlowStartService startService = new FlowStartService(
    workCode,           // 流程编码
    operator,           // 发起人 (IFlowOperator)
    bindData,           // 绑定数据 (IBindData)
    advice,             // 发起意见
    repositoryHolder    // 仓储持有者
);

FlowResult result = startService.startFlow();
```

`startFlow()` 的执行流程：
1. 加载并验证流程定义（`loadFlowWork`）
2. 创建或获取流程版本快照（`loadFlowBackup`）
3. 保存流程实例（`saveFlowProcess`）
4. 保存绑定数据快照（`saveBindDataSnapshot`）
5. 构建节点服务并创建待办记录（`buildFlowNodeService` → `createRecord`）
6. 推送 `FlowApprovalEvent` 事件（STATE_CREATE / STATE_TODO / STATE_SAVE / STATE_FINISH）

### 6. 提交审批

使用 `FlowSubmitService` 处理审批操作：

```java
FlowSubmitService submitService = new FlowSubmitService(
    recordId,           // 当前审批记录ID
    currentOperator,    // 当前操作者
    bindData,           // 绑定数据
    opinion,            // 审批意见 (Opinion)
    repositoryHolder    // 仓储持有者
);
```

`Opinion` 封装了审批动作：
- `Opinion.pass(advice)` — 通过
- `Opinion.reject(advice)` — 拒绝
- `Opinion.transfer(operatorIds)` — 转办
- 支持设置自定义操作者 `opinion.getOperatorIds()`

### 7. 查看流程步骤预览

使用 `FlowStepService` 获取流程步骤预览信息：

```java
FlowStepService stepService = new FlowStepService(
    recordId,           // 记录ID（0 表示新流程预览）
    workCode,           // 流程编码
    currentOperator,    // 当前操作者
    bindData,           // 绑定数据
    repositoryHolder    // 仓储持有者
);

FlowStepResult stepResult = stepService.getFlowStep();
```

### 8. 监听流程事件

实现 `IHandler<FlowApprovalEvent>` 响应流程状态变更：

```java
@Component
public class FlowEventHandler implements IHandler<FlowApprovalEvent> {

    @Override
    public void handler(FlowApprovalEvent event) {
        if (event.isCreate()) {
            // 流程创建
        } else if (event.isTodo()) {
            // 新待办产生，发送通知
        } else if (event.isPass()) {
            // 审批通过
        } else if (event.isReject()) {
            // 审批拒绝
        } else if (event.isFinish()) {
            // 流程结束
        } else if (event.isTransfer()) {
            // 转办
        } else if (event.isCirculate()) {
            // 抄送
        }
        // ... 其他状态
    }
}
```

`FlowApprovalEvent` 支持的状态常量：

| 常量 | 值 | 含义 |
|------|-----|------|
| `STATE_CREATE` | 1 | 创建流程 |
| `STATE_PASS` | 2 | 审批通过 |
| `STATE_REJECT` | 3 | 审批拒绝 |
| `STATE_TRANSFER` | 4 | 转办 |
| `STATE_RECALL` | 5 | 撤销 |
| `STATE_FINISH` | 6 | 流程完成 |
| `STATE_TODO` | 7 | 创建待办 |
| `STATE_URGE` | 8 | 催办 |
| `STATE_CIRCULATE` | 9 | 抄送 |
| `STATE_SAVE` | 10 | 保存 |
| `STATE_DELETE` | 11 | 删除 |
| `STATE_BACK` | 12 | 退回 |
| `STATE_VOIDED` | 13 | 作废 |
| `STATE_STOP` | 14 | 停止 |

## 使用实例

### 流程流转方向控制

`FlowDirectionService` 负责解析审批方向：

```java
FlowDirectionService directionService = 
    new FlowDirectionService(flowNode, flowWork, opinion);
directionService.bindHistoryRecords(historyRecords);
directionService.loadFlowSourceDirection();

// 判断是否为通过方向
if (directionService.isPassRecord()) {
    flowNodeService.loadNextPassNode(currentNode);
} else {
    flowNodeService.loadDefaultBackNode(currentNode, currentRecord);
}
```

### 会签处理

对于会签节点（`flowNode.isSign() == true`），`FlowDirectionService.reloadFlowSourceDirection()` 会根据历史记录中所有人的提交状态重新计算审批方向——只有所有人通过后才会继续流转，否则转为拒绝方向。

### 流程版本快照

```java
// 自动备份（在 FlowStartService 中调用）
FlowBackup backup = flowBackupRepository.backup(flowWork);

// 从快照恢复流程定义
FlowWork restoredWork = backup.resume(flowOperatorRepository);
```

`FlowBackup` 将 `FlowWork` 序列化为字节数组存储，恢复时通过 `FlowWorkSerializable.fromSerializable(bytes).toFlowWork()` 反序列化，确保运行中的流程不受后续流程定义修改的影响。

### FlowSession 上下文

`FlowSession` 封装了流程运行时的完整上下文，供 `TitleGenerator`、`OperatorMatcher`、`ErrTrigger`、`FlowRelation.trigger()` 等脚本化组件使用：

```java
FlowSession session = new FlowSession(
    flowRecord,       // 当前审批记录
    flowWork,         // 流程定义
    flowNode,         // 当前节点
    createOperator,   // 发起人
    currentOperator,  // 当前操作者
    bindData,         // 绑定数据
    opinion,          // 审批意见
    historyRecords    // 历史记录
);
```

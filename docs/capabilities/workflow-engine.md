---
name: workflow-engine
description: 工作流引擎，支持流程定义、节点流转、审批、委托、会签、数据快照和事件通知
status: 已实现
scope: 后端
source: 项目自有
last_commit: 0fc02aca
code_files:
  - springboot-starter-flow/src/main/java/com/codingapi/springboot/flow/FlowConfiguration.java
  - springboot-starter-flow/src/main/java/com/codingapi/springboot/flow/record/FlowProcess.java
  - springboot-starter-flow/src/main/java/com/codingapi/springboot/flow/record/FlowRecord.java
  - springboot-starter-flow/src/main/java/com/codingapi/springboot/flow/record/FlowBackup.java
  - springboot-starter-flow/src/main/java/com/codingapi/springboot/flow/record/FlowMerge.java
  - springboot-starter-flow/src/main/java/com/codingapi/springboot/flow/service/FlowService.java
  - springboot-starter-flow/src/main/java/com/codingapi/springboot/flow/service/FlowNodeService.java
  - springboot-starter-flow/src/main/java/com/codingapi/springboot/flow/service/impl/FlowStartService.java
  - springboot-starter-flow/src/main/java/com/codingapi/springboot/flow/service/impl/FlowSubmitService.java
  - springboot-starter-flow/src/main/java/com/codingapi/springboot/flow/service/impl/FlowRecallService.java
  - springboot-starter-flow/src/main/java/com/codingapi/springboot/flow/service/impl/FlowStopService.java
  - springboot-starter-flow/src/main/java/com/codingapi/springboot/flow/service/impl/FlowTrySubmitService.java
  - springboot-starter-flow/src/main/java/com/codingapi/springboot/flow/repository/FlowWorkRepository.java
  - springboot-starter-flow/src/main/java/com/codingapi/springboot/flow/repository/FlowProcessRepository.java
  - springboot-starter-flow/src/main/java/com/codingapi/springboot/flow/repository/FlowRecordRepository.java
  - springboot-starter-flow/src/main/java/com/codingapi/springboot/flow/repository/FlowBackupRepository.java
---

## 解决什么问题

企业级审批场景（请假、报销、采购等）需要标准化的流程引擎来管理：
- **流程定义与版本管理**：流程模板可修改、可版本化，运行中流程不受定义变更影响
- **节点流转规则**：支持串行、并行（会签）、条件分支等多种流转模式
- **审批操作**：通过、驳回、退回、撤回、转办、催办、终止等完整操作集
- **数据快照**：发起时锁定流程定义版本，确保审批过程一致性
- **事件通知**：每个状态变更自动推送 `FlowApprovalEvent`，驱动业务联动

## 如何使用

### 核心数据模型

```
FlowWork（流程定义）
    └── FlowNode（节点）
        └── FlowSource（连线/分支条件）

FlowProcess（流程实例）
    └── FlowRecord（审批记录）
        └── FlowBackup（版本快照）
```

| 模型 | 说明 |
|------|------|
| `FlowWork` | 流程模板定义，包含节点列表和连线关系 |
| `FlowProcess` | 流程实例（由 `FlowStartService` 创建），关联 backupId 锁定版本 |
| `FlowRecord` | 审批记录，记录每个节点的审批人、结果、意见、时间 |
| `FlowBackup` | 流程定义快照（Kryo 序列化），确保运行中流程不受模板修改影响 |

### 服务层 API

| 服务 | 方法 | 说明 |
|------|------|------|
| `FlowStartService` | `startFlow(...)` | 发起流程，创建 FlowProcess + FlowBackup + 初始 FlowRecord |
| `FlowSubmitService` | `submitFlow(...)` | 提交审批（通过/驳回） |
| `FlowRecallService` | `recallFlow(...)` | 撤回已提交的流程 |
| `FlowTrySubmitService` | `trySubmitFlow(...)` | 试提交 — 获取下一节点审批人（不实际流转） |
| `FlowStopService` | `stopFlow(...)` | 终止流程 |
| `FlowUrgeService` | `urgeFlow(...)` | 催办 |
| `FlowNodeService` | `getFlowRecords(...)` | 获取流程审批记录 |
| `FlowStepService` | `getFlowSteps(...)` | 获取流程步骤图 |
| `FlowCustomEventService` | `customEvent(...)` | 触发自定义按钮事件 |

### 流程状态（FlowStatus）

```
RUNNING → FINISH / VOIDED / STOP
```

### 审批操作类型（FlowType）

CREATE、SAVE、PASS、REJECT、RECALL、DELETE、VOIDED、BACK、FINISH、TRANSFER、URGE、STOP

### 事件通知

每次状态变更通过 `EventPusher` 推送 `FlowApprovalEvent`，业务侧通过 `IHandler<FlowApprovalEvent>` 监听：

```java
@Component
public class LeaveHandler implements IHandler<FlowApprovalEvent> {
    @Override
    public void handler(FlowApprovalEvent event) {
        if (event.getFlowType() == FlowType.FINISH) {
            // 审批完成 — 更新请假状态
        }
    }
}
```

### 配置

```java
@Configuration
public class FlowConfiguration {
    // 自动注册所有 Flow Repository 和 Service
    // 通过 FlowFrameworkRegister 注册框架级触发器
}
```

## 使用实例

```java
// 1. 发起请假流程
FlowProcess process = flowStartService.startFlow(
    workId,        // 流程定义ID
    operatorId,    // 发起人ID
    bindDataId     // 关联业务数据ID
);

// 2. 试提交 — 查看下一节点审批人
List<IFlowOperator> nextApprovers = flowTrySubmitService.trySubmitFlow(
    process.getProcessId(), operatorId, "同意请假"
);

// 3. 正式提交审批
flowSubmitService.submitFlow(
    process.getProcessId(), operatorId, "同意请假", ApprovalType.PASS
);

// 4. 撤回（仅在下一节点未审批前可撤回）
flowRecallService.recallFlow(process.getProcessId(), operatorId);

// 5. 查询审批记录
List<FlowRecord> records = flowNodeService.getFlowRecords(process.getProcessId());

// 6. 终止流程
flowStopService.stopFlow(process.getProcessId(), operatorId, "业务取消");
```

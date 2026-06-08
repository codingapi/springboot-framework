---
name: workflow-engine
description: 轻量级工作流引擎，支持流程定义、节点流转、审批、退回、委托、会签、抄送、数据快照与事件通知
status: 已实现
scope: 后端
source: 项目自有
import: com.codingapi.springboot:springboot-starter-flow
symbols:
  - FlowWork
  - FlowNode
  - FlowRelation
  - FlowRecord
  - FlowBackup
  - FlowProcess
  - FlowWorkBuilder
  - SchemaReader
  - FlowNodeService
  - FlowStartService
  - FlowStepService
  - FlowSubmitService
  - FlowBackService
  - FlowRecallService
  - FlowTransferService
  - FlowSession
  - FlowApprovalEvent
  - TitleGenerator
  - OperatorMatcher
  - OutTrigger
  - IFlowOperator
  - FlowRecordRepository
  - FlowWorkRepository
  - FlowOperatorRepository
  - BindDataSnapshot
  - IBindData
content_hash: 42954fc1cb16f19b5ed4693dfa2ba2887cc9a90673dbb5bafd1dd7c291631712
---

## 解决什么问题

企业应用中审批流程（请假、报销、合同审批等）是高频需求。本工作流引擎解决了以下问题：

- **流程定义与构建**：通过 `FlowWorkBuilder` 或 JSON Schema 定义流程节点和流转关系
- **灵活的节点流转**：支持条件分支、退回、委托、抄送、会签等多种流转模式
- **操作者匹配**：通过 Groovy 脚本动态匹配节点审批人
- **数据快照**：使用 Kryo 深拷贝保存审批时的业务数据快照
- **事件驱动**：每个状态变更推送 `FlowApprovalEvent`，与事件系统集成

## 如何使用

### 核心领域模型

- `FlowWork` — 流程定义（包含节点和关系）
- `FlowNode` — 流程节点（开始/审批/传阅/结束）
- `FlowRelation` — 节点间关系（含条件触发器）
- `FlowRecord` — 审批记录
- `FlowBackup` — 流程版本快照

### 流程发起

```java
@Autowired
private FlowService flowService;

// 发起流程
FlowResult result = flowService.start("leave-approval", bindData, createOperator);
```

### 流程审批

```java
// 提交审批意见
Opinion opinion = Opinion.builder()
    .pass(true)
    .comment("同意")
    .build();
flowService.submit(processId, opinion, currentOperator);
```

### 流程退回

```java
flowService.back(processId, opinion, currentOperator);
```

### 监听审批事件

```java
@Service
public class LeaveHandler implements IHandler<FlowApprovalEvent> {
    @Override
    public void handler(FlowApprovalEvent event) {
        if (event.isFinish() && event.match(LeaveForm.class)) {
            // 审批通过，执行业务逻辑
        }
    }
}
```

## 使用实例

参考 `example` 模块中的请假流程示例：
- 流程定义：`FlowWorkCmd` 通过 `FlowWorkBuilder` 构建
- 审批处理：`LeaveHandler` 监听 `FlowApprovalEvent`
- 数据绑定：`LeaveForm implements IBindData`

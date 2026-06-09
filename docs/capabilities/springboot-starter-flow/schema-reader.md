---
name: springboot-starter-flow/schema-reader
module: springboot-starter-flow
description: 流程设计图（Schema）解析器，将 JSON 描述的节点/边转换为 FlowNode/FlowRelation 内存模型
status: 已实现
scope: 后端
source: 项目自有
import: "com.codingapi.springboot:springboot-starter-flow"
symbols:
  - SchemaReader
content_hash: 8c99d36738692cd511d97859cae931de1de84b46d3957b02a48de144cd44f640
---

## 解决什么问题

前端流程设计器（flow-pc / flow-mobile）输出的 JSON 包含 `nodes[]` 和 `edges[]` 数组，每个节点/边带有 `properties.code/operatorMatcher/titleGenerator/...` 等配置。业务后端需要把这套 JSON 解析为强类型的 `FlowNode` / `FlowRelation` 对象，并支持：

- 节点类型识别（`NodeType.parser(type)`：开始/结束/审批/抄送等）
- 审批人策略（`OperatorMatcher`：基于用户/角色/部门/发起人等）
- 标题动态生成（`TitleGenerator`：Groovy 脚本）
- 边上的出方向触发器（`OutTrigger`）
- 错误处理策略（`ErrTrigger`）
- 节点按钮（`FlowButton[]`）

## 如何使用

**1. 解析 Schema JSON**

```java
String schemaJson = "{...nodes:[...], edges:[...]}";
SchemaReader reader = new SchemaReader(schemaJson);

List<FlowNode> nodes = reader.getFlowNodes();
List<FlowRelation> relations = reader.getFlowRelations();

// 与 FlowWork 配合使用（推荐）
FlowWork flow = new FlowWork(currentUser);
flow.schema(schemaJson);  // 内部使用 SchemaReader，并自动 verify()
```

**2. Schema JSON 格式示例**

```json
{
  "nodes": [
    {
      "properties": {
        "id": "node-1",
        "code": "start",
        "name": "发起",
        "type": "START",
        "operatorMatcher": "user:#{currentUser}",
        "titleGenerator": "return '订单 '+orderNo+' 待发起'",
        "editable": true,
        "mergeable": false,
        "view": "/form/order",
        "approvalType": "AUTO",
        "timeout": 0,
        "errTrigger": "log:error",
        "buttons": [
          { "code": "submit", "name": "提交", "style": "primary" }
        ]
      }
    }
  ],
  "edges": [
    {
      "id": "edge-1",
      "sourceNodeId": "node-1",
      "targetNodeId": "node-2",
      "properties": {
        "name": "提交",
        "outTrigger": "default",
        "back": false,
        "order": 1
      }
    }
  ]
}
```

**3. 字段说明**

| 字段 | 含义 | 对应类型 |
|------|------|----------|
| `code` | 节点代码（`start`/`over` 是保留字） | `String` |
| `type` | 节点类型 | `NodeType`（START/OVER/APPROVAL/CC/...） |
| `approvalType` | 审批类型 | `ApprovalType`（AUTO/ANY/ALL/ORDER） |
| `operatorMatcher` | 审批人策略（Groovy） | `OperatorMatcher` |
| `titleGenerator` | 标题脚本（Groovy） | `TitleGenerator` |
| `errTrigger` | 错误处理（可选） | `ErrTrigger` |
| `outTrigger` | 出边触发器（默认 `default`） | `OutTrigger` |
| `back` | 是否可回退 | `boolean` |
| `order` | 边的执行顺序 | `int` |

## 使用实例

```java
// 1. 接收前端设计器提交
@PostMapping("/flow/save")
public FlowWork save(@RequestBody FlowDesignDTO dto) {
    FlowWork flow = new FlowWork(currentUser);
    flow.setTitle(dto.getTitle());
    flow.setCode(dto.getCode());
    flow.schema(dto.getSchema());  // 内部自动 verify()
    return flowRepository.save(flow);
}

// 2. 复制流程（同时重新生成节点/边 ID）
FlowWork copy = flow.copy();  // 内部调用 SchemaReader

// 3. 配合验证逻辑
flow.verify();  // 校验：节点不为空、关系不为空、title/code 非空、起始/结束节点存在等
```

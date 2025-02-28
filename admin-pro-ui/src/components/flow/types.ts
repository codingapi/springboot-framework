// 节点状态
export type NodeState = "done" | "wait" | "undone" | "current";

// 节点类型
export type NodeType = 'start-node' | 'node-node' | 'over-node' | 'circulate-node';

// 自定义按钮类型
export type CustomButtonType =
    | 'RELOAD'  // 重新加载
    | 'SAVE'    // 保存
    | 'START'   // 发起
    | 'SUBMIT'  // 提交
    | 'TRY_SUBMIT'  // 预提交
    | 'SPECIFY_SUBMIT'  // 指定人员提交
    | 'REJECT'  // 驳回
    | 'TRANSFER'    // 转办
    | 'RECALL'  // 撤销
    | 'POSTPONED'   // 延期
    | 'URGE'    // 催办
    | 'CUSTOM'  // 自定义后端接口
    | 'VIEW'    // 自定义前端事件
    | 'REMOVE'; // 删除

// 节点属性
export interface NodeProperties {
    name: string;
    code: string;
    type:string;
    view: string;
    operatorMatcher:string;
    editable:boolean;
    titleGenerator:string;
    errTrigger:string;
    approvalType:string;
    timeout:number;

    settingVisible?: boolean;
    flowState?: NodeState;
}

// 节点按钮属性
export interface NodeButtonProperties {
    id:string;
    name:string;
    type:CustomButtonType;
    style:string;
    order:number;
    groovy:string;
    eventKey:string;
}

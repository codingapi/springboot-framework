// 节点状态
export type NodeState = "done" | "wait" | "undone" | "current";

// 节点类型
export type NodeType = 'start-node' | 'node-node' | 'over-node' | 'circulate-node';


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

// 节点状态
import {FormAction} from "@/components/form";

export type NodeState = "done" | "wait" | "undone" | "current";

// 节点类型
export type NodeType = 'start-node' | 'node-node' | 'over-node' | 'circulate-node';

// 流程图中线的类型
export type EdgeType = 'line' | 'polyline' | 'bezier';


// 延期表单视图Key
export const PostponedFormViewKey = 'PostponedFormView';
// 选人表单视图Key
export const UserSelectFormViewKey = 'UserSelectFormViewKey';


// 延期表单 【拓展视图】
export interface PostponedFormProps {
    visible: boolean;
    setVisible: (visible: boolean) => void;
    onFinish: (timeout: number) => void;
}


// 选人表单 【拓展视图】
export type UserSelectFormType =
// 选择下级流程节点的人员，约定人员id范围
    'nextNodeUser'
    // 选择转办人员，约定本单位下的人员
    | 'transfer'
    // 选择所有人员，在流程配置上使用
    | 'users';

export interface UserSelectFormProps {
    visible: boolean;
    setVisible: (visible: boolean) => void;
    onFinish: (values: FlowUser[]) => void;
    // 选择模式
    multiple: boolean;
    // 指定人员范围
    specifyUserIds?: number[];
    // 当前选择的人员
    currentUserIds?: number[];
    // 选人方式
    userSelectType: UserSelectFormType;
}

// 流程用户
export interface FlowUser {
    id: string;
    name: string;

    [key: string]: any;
}


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


// 流程设置面板属性
export interface SettingPanelProps {
    visible: boolean;
    setVisible: (visible: boolean) => void;
    properties: any;
    onSettingChange: (values: any) => void;
}

// 表单参数
export interface FlowFormParams {
    clazzName: string;

    [key: string]: any;
}

// 流程表单数据
export interface FlowFormViewProps {
    // 表单数据
    data: FlowFormParams;
    // 表单控制对象
    formAction: React.RefObject<FormAction>;
    // 数据版本
    dataVersion?: number;
}

// 表单视图
export interface FlowFormView {
    [key: string]: React.ComponentType<FlowFormViewProps>;
}

export interface FlowViewProps {
    // 流程编号
    id?: string;
    // 流程的设计编号
    workCode?: string;
    // 流程的视图数据
    view: React.ComponentType<FlowFormViewProps> | FlowFormView;
    // 表单参数，参数仅当在发起节点时才会传递
    formParams?: FlowFormParams;
}

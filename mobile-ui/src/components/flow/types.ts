import React from "react";
import {FormAction} from "@/components/form";


// 流程图中线的类型
export type EdgeType = 'line' | 'polyline' | 'bezier';

// 延期表单视图Key
export const PostponedFormViewKey = 'PostponedFormView';
// 选人表单视图Key
export const UserSelectFormViewKey = 'UserSelectFormViewKey';

// 自定义按钮类型
export type ButtonType =
    'RELOAD'
    | 'SAVE'
    | 'START'
    | 'SUBMIT'
    | 'TRY_SUBMIT'
    | 'SPECIFY_SUBMIT'
    | 'REJECT'
    | 'TRANSFER'
    | 'RECALL'
    | 'POSTPONED'
    | 'URGE'
    | 'CUSTOM'
    | 'VIEW'
    | 'REMOVE';

// 流程自定义按钮
export interface FlowButton {
    id: string;
    eventKey: string;
    groovy: string;
    name: string;
    order: number;
    style: any;
    type: ButtonType;
}

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


// 结果展示数据项目
export interface FlowResultItem {
    label: string,
    value: string
}

type FlowResultMessageState = 'success'|'info'|'warning';

// 结果展示数据信息
export interface FlowResultMessage {
    title: string,
    closeable: boolean,
    state: FlowResultMessageState,
    items?: FlowResultItem[]
}

// 流程用户
export interface FlowUser {
    id: string;
    name: string;

    [key: string]: any;
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


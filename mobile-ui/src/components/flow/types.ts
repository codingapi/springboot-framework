import React from "react";
import {FormAction} from "@/components/form";


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


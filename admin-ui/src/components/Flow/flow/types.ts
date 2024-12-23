import React from "react";
import {FormInstance} from "antd/es/form/hooks/useForm";
import {FlowData} from "@/components/Flow/flow/data";

// 自定义按钮类型
export type CustomButtonType =
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
    | 'VIEW';

// 流程图中线的类型
export type EdgeType = 'line' | 'polyline' | 'bezier';

// 表单视图属性
export interface FlowFormViewProps {
    // 表单数据
    data: FlowFormParams;
    // 表单实例
    form: FormInstance<any>;
    // 展示状态
    visible: boolean;
    // 是否编辑
    editable: boolean;
    // 是否对比
    compare: boolean;
    // 审批意见
    opinions?: any;
    // 流程详情数据信息
    flowData?: FlowData;
    // 流程交互操作
    handlerClick?: (item: {
        // 按钮类型
        type: CustomButtonType;
        // 按钮的Id
        id?: string;
        // 操作参数，对应提交时指定人员数组，例如[1,2,3]
        params?: any;
    }) => void;

    // 请求数据加载
    requestLoading?: boolean;
    // 设置请求数据加载状态
    setRequestLoading?: (loading: boolean) => void;

    // 自定义前端点击事件触发事件
    eventKey?: string;

    // 审批意见输入框
    opinionEditorVisible?: (visible: boolean) => void;
}

// 表单视图
export interface FlowFormView {
    [key: string]: React.ComponentType<FlowFormViewProps>;
}

// 表单参数
export interface FlowFormParams {
    clazzName: string;

    [key: string]: any;
}

// 关闭结果视图事件
export const EVENT_CLOSE_RESULT_VIEW = 'EVENT_CLOSE_RESULT_VIEW';
// 重新加载数据事件
export const EVENT_RELOAD_DATA = 'EVENT_RELOAD_DATA';

export const PostponedFormViewKey = 'PostponedFormView';

// 延期表单 【拓展视图】
export interface PostponedFormProps {
    visible: boolean;
    setVisible: (visible: boolean) => void;
    onFinish: (values: any) => void;
}

export const ResultFormViewKey = 'ResultFormView';


export interface FlowResultMessage {
    title: string,
    resultState: string,
    items: FlowResultItem[]
}

export interface FlowResultItem {
    label: string,
    value: string
}

// 结果表单 【拓展视图】
export interface ResultFormProps {
    visible: boolean;
    setVisible: (visible: boolean) => void;
    // 是否关闭流程框
    flowCloseable: boolean;
    closeFlow: () => void;

    result: FlowResultMessage | null;
}

export const UserSelectViewKey = 'UserSelectView';

export type UserSelectMode = 'single' | 'multiple';

export type UserSelectType =
// 选择下级流程节点的人员，约定人员id范围
    'nextNodeUser'
    // 选择转办人员，约定本单位下的人员
    | 'transfer'
    // 选择所有人员，在流程配置上使用
    | 'users';

export interface FlowUser {
    id: string;
    name: string;

    [key: string]: any;
}

// 选人表单 【拓展视图】

export interface UserSelectProps {
    visible: boolean;
    setVisible: (visible: boolean) => void;
    onFinish: (values: FlowUser[]) => void;
    mode: UserSelectMode;
    // 指定人员范围
    specifyUserIds: number[];
    // 当前选择的人员
    currentUserIds?: number[];
    // 选人类型
    userSelectType: UserSelectType;
}

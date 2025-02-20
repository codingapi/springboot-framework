import React from "react";


export interface FlowFormViewProps {

}

export interface FlowViewProps {
    view: React.ComponentType<FlowFormViewProps> | FlowFormView;
}

// 表单视图
export interface FlowFormView {
    [key: string]: React.ComponentType<FlowFormViewProps>;
}

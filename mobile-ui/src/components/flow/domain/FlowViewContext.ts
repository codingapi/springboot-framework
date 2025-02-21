import {FlowButton, FlowFormView, FlowViewProps} from "@/components/flow/types";

export class FlowViewContext {

    private readonly props: FlowViewProps;
    private readonly data: any;

    constructor(props: FlowViewProps, data?: any) {
        this.props = props;
        this.data = data;
    }

    // 获取流程的工作编码
    getWorkCode() {
        return this.data.flowWork.code;
    }

    // 获取流程的Form视图
    getFlowFormView() {
        const view = this.props.view;
        if (typeof view === 'object') {
            const nodeView = this.data.flowNode.view;
            return (view as FlowFormView)[nodeView];
        }
        return view;
    }

    //获取流程的form数据
    getFlowFormParams() {
        return {
            ...this.data.bindData,
            ...this.props.formParams
        }
    }

    // 获取流程的操作按钮
    getFlowButtons() {
        const buttons = this.data.flowNode.buttons as FlowButton[] || [];
        return buttons.sort((item1: any, item2: any) => {
            return item1.order - item2.order;
        })
    }

}

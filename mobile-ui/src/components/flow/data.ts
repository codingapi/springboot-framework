import {FlowButton, FlowFormView, FlowViewProps} from "@/components/flow/types";

export class FlowViewContext {

    private readonly props: FlowViewProps;
    private readonly data: any;

    constructor(props: FlowViewProps, data?: any) {
        this.props = props;
        this.data = data;
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
    getFlowFormParams(){
        return {
            ...this.data.bindData,
            ...this.props.formParams
        }
    }

    // 获取流程的操作按钮
    getFlowButtons() {
        return this.data.flowNode.buttons as FlowButton[] || [];
    }

}


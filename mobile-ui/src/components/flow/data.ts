import {FlowFormView, FlowViewProps} from "@/components/flow/types";

export class FlowViewContext {

    private readonly props: FlowViewProps;
    private readonly data: any;

    constructor(props: FlowViewProps, data?: any) {
        this.props = props;
        this.data = data;
    }

    getFlowFormView() {
        const view = this.props.view;
        if (this.data) {
            const nodeView = this.data.nodeView;
            if (typeof view === 'object') {
                return (view as FlowFormView)[nodeView];
            }
        }
        return view;
    }

}


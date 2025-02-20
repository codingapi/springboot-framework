import React from "react";
import {Provider} from "react-redux";
import {flowStore} from "@/components/flow/store/FlowSlice";
import {FlowFormViewProps, FlowViewProps} from "@/components/flow/types";
import {Tabs} from "antd-mobile";
import {FlowViewContext} from "@/components/flow/data";
import "./index.scss";

const $FlowView: React.FC<FlowViewProps> = (props) => {

    const flowViewContext = new FlowViewContext(props);

    const FlowFormView = flowViewContext.getFlowFormView() as React.ComponentType<FlowFormViewProps>;

    return (
        <div className={"flow-view"}>
            <Tabs
            >
                <Tabs.Tab title='详情' key='detail'>
                    <FlowFormView/>
                </Tabs.Tab>
                <Tabs.Tab title='流程' key='flow'>
                    流程详情
                </Tabs.Tab>
            </Tabs>
        </div>
    )
}

const FlowView: React.FC<FlowViewProps> = (props) => {
    return (
        <Provider store={flowStore}>
            <$FlowView {...props} />
        </Provider>
    )
}

export default FlowView;

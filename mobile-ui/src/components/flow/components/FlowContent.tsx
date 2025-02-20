import React, {useContext} from "react";
import {Tabs} from "antd-mobile";
import {FlowFormViewProps} from "@/components/flow/types";
import {FlowViewReactContext} from "@/components/flow/view";
import FlowHistory from "@/components/flow/components/FlowHistory";

const FlowContent = () => {
    const flowViewReactContext = useContext(FlowViewReactContext);
    if (!flowViewReactContext) {
        return <></>;
    }
    const flowViewContext = flowViewReactContext.flowViewContext;
    const formAction = flowViewReactContext.formAction;

    const FlowFormView = flowViewContext.getFlowFormView() as React.ComponentType<FlowFormViewProps>;

    const formParams = flowViewContext.getFlowFormParams();

    return (
        <div className={"flow-view-content"}>
            <Tabs>
                <Tabs.Tab title='详情' key='detail'>
                    <FlowFormView
                        data={formParams}
                        formAction={formAction}
                    />
                </Tabs.Tab>
                <Tabs.Tab title='流程' key='flow'>
                    <FlowHistory/>
                </Tabs.Tab>
            </Tabs>
        </div>
    )
}

export default FlowContent;

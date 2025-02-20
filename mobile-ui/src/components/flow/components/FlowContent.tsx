import React, {useContext} from "react";
import {Tabs} from "antd-mobile";
import {FlowFormViewProps} from "@/components/flow/types";
import {FlowViewReactContext} from "@/components/flow/view";
import FlowHistory from "@/components/flow/components/FlowHistory";
import {FormAction} from "@/components/form";

const FlowContent = () => {
    const flowViewContext = useContext(FlowViewReactContext);
    if (!flowViewContext) {
        return <></>;
    }

    const FlowFormView = flowViewContext.getFlowFormView() as React.ComponentType<FlowFormViewProps>;

    const formParams = flowViewContext.getFlowFormParams();

    const formAction = React.useRef<FormAction>(null);

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

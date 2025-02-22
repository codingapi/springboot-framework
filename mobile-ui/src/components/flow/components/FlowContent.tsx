import React, {useContext, useEffect} from "react";
import {Tabs} from "antd-mobile";
import {FlowFormViewProps} from "@/components/flow/types";
import {FlowViewReactContext} from "@/components/flow/view";
import FlowHistory from "@/components/flow/components/FlowHistory";
import FlowOpinion from "@/components/flow/components/FlowOpinion";
import {useSelector} from "react-redux";
import {FlowReduxState} from "@/components/flow/store/FlowSlice";

interface FlowContentProps {
}

const FlowContent:React.FC<FlowContentProps> = (props) => {
    const flowViewReactContext = useContext(FlowViewReactContext);

    const flowViewContext = flowViewReactContext?.flowViewContext;
    const formAction = flowViewReactContext?.formAction;

    const FlowFormView = flowViewContext?.getFlowFormView() as React.ComponentType<FlowFormViewProps>;

    const formParams = flowViewContext?.getFlowFormParams();

    const opinionVisible = useSelector((state: FlowReduxState) => state.flow.opinionVisible);
    const contentHiddenVisible = useSelector((state: FlowReduxState) => state.flow.contentHiddenVisible);

    useEffect(() => {
        if(!flowViewContext?.isEditable()){
            setTimeout(()=>{
                formAction?.current?.disableAll();
            },100);
        }
    }, []);

    const style = contentHiddenVisible ? {"display":"none"} : {};
    return (
        <div className={"flow-view-content"} style={style}>
            <Tabs>
                <Tabs.Tab title='详情' key='detail'>
                    {formAction && (
                        <FlowFormView
                            data={formParams}
                            formAction={formAction}
                        />
                    )}

                    {opinionVisible && (
                        <FlowOpinion/>
                    )}
                </Tabs.Tab>
                <Tabs.Tab title='流程' key='flow'>
                    <FlowHistory/>
                </Tabs.Tab>
            </Tabs>
        </div>
    )
}

export default FlowContent;

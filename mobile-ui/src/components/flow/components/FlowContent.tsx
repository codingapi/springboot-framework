import React, {useContext, useEffect} from "react";
import {Divider, Tabs} from "antd-mobile";
import {FlowFormViewProps} from "@/components/flow/types";
import {FlowViewReactContext} from "@/components/flow/view";
import FlowHistory from "@/components/flow/components/FlowHistory";
import FlowFormOpinion from "@/components/flow/components/FlowFormOpinion";
import {useSelector} from "react-redux";
import {FlowReduxState} from "@/components/flow/store/FlowSlice";
import FlowChart from "@/components/flow/components/FlowChart";
import FlowHistoryLine from "@/components/flow/components/FlowHistoryLine";
import FlowOpinion from "@/components/flow/components/FlowOpinion";

const FlowContent= () => {
    const flowViewReactContext = useContext(FlowViewReactContext);

    const flowRecordContext = flowViewReactContext?.flowRecordContext;
    const formAction = flowViewReactContext?.formAction;

    const FlowFormView = flowRecordContext?.getFlowFormView() as React.ComponentType<FlowFormViewProps>;

    const formParams = flowRecordContext?.getFlowFormParams();

    const opinionVisible = useSelector((state: FlowReduxState) => state.flow.opinionVisible);
    const dataVersion = useSelector((state: FlowReduxState) => state.flow.dataVersion);
    const contentHiddenVisible = useSelector((state: FlowReduxState) => state.flow.contentHiddenVisible);

    useEffect(() => {
        if(!flowRecordContext?.isEditable()){
            setTimeout(()=>{
                formAction?.current?.disableAll();
            },100);
        }
    }, []);

    const style = contentHiddenVisible ? {"display":"none"} : {};
    return (
        <div className={"flow-view-content"} style={style}>
            <Tabs>
                <Tabs.Tab title='流程详情' key='detail'>
                    {formAction && (
                        <FlowFormView
                            data={formParams}
                            formAction={formAction}
                            dataVersion={dataVersion}
                        />
                    )}

                    {opinionVisible && (
                        <FlowFormOpinion/>
                    )}
                </Tabs.Tab>
                <Tabs.Tab title='流程记录' key='record'>
                    <FlowHistory/>
                    <Divider>审批记录</Divider>
                    <FlowOpinion/>
                </Tabs.Tab>
                <Tabs.Tab title='流程图' key='chart'>
                    <FlowChart/>
                    <Divider>流转历史</Divider>
                    <FlowHistoryLine/>
                </Tabs.Tab>
            </Tabs>
        </div>
    )
}

export default FlowContent;

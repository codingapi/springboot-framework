import React, {useContext, useEffect} from "react";
import {FlowFormViewProps} from "@/components/flow/types";
import {FlowViewReactContext} from "@/components/flow/view";
import {useSelector} from "react-redux";
import {FlowReduxState} from "@/components/flow/store/FlowSlice";
import {Tabs, TabsProps} from "antd";

const FlowContent = () => {
    const flowViewReactContext = useContext(FlowViewReactContext);

    const flowRecordContext = flowViewReactContext?.flowRecordContext;
    const formInstance = flowViewReactContext?.formInstance;

    const FlowFormView = flowRecordContext?.getFlowFormView() as React.ComponentType<FlowFormViewProps>;

    const formParams = flowRecordContext?.getFlowFormParams();

    const opinionVisible = useSelector((state: FlowReduxState) => state.flow.opinionVisible);
    const dataVersion = useSelector((state: FlowReduxState) => state.flow.dataVersion);
    const contentHiddenVisible = useSelector((state: FlowReduxState) => state.flow.contentHiddenVisible);

    useEffect(() => {
        if (!flowRecordContext?.isEditable()) {
            setTimeout(() => {
                formInstance?.disableAll();
            }, 100);
        }
    }, []);

    const style = contentHiddenVisible ? {"display": "none"} : {};

    const items = [
        {
            label: '流程详情',
            key: 'detail'
        },
        {
            label: '流程记录',
            key: 'record'
        },
        {
            label: '流程图',
            key: 'chart'
        }
    ] as TabsProps['items'];

    return (
        <div className={"flow-view-content"} style={style}>
            <Tabs items={items}/>
        </div>
    )
}

export default FlowContent;

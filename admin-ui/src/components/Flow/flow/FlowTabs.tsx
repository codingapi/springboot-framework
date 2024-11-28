import React from "react";
import {FlowData} from "@/components/Flow/flow/data";
import {Skeleton, Tabs} from "antd";
import {FlowFormView, FlowFormViewProps} from "@/components/Flow/flow/types";
import {FormInstance} from "antd/es/form/hooks/useForm";
import FlowHistory from "@/components/Flow/flow/FlowHistory";
import FlowChart from "@/components/Flow/flow/FlowChart";
import FlowDetail from "@/components/Flow/flow/FlowDetail";


interface FlowTabsProps {
    flowData: FlowData;
    view: React.ComponentType<FlowFormViewProps> | FlowFormView;
    visible: boolean;
    form: FormInstance<any>;
    adviceForm: FormInstance<any>;
    // 预览模式
    review?: boolean;
}

const FlowTabs: React.FC<FlowTabsProps> = (props) => {

    const flowData = props.flowData;

    if (!flowData.hasData()) {
        return (
            <Tabs
                className="view-flow-tabs"
                items={[
                    {
                        key: 'flow',
                        label: '流程详情',
                        children: (
                            <Skeleton loading={true}>
                                <></>
                            </Skeleton>
                        ),
                    },
                    {
                        key: 'history',
                        label: '流程记录',
                        children: (
                            <Skeleton loading={true}>
                                <></>
                            </Skeleton>
                        ),
                    },
                    {
                        key: 'chart',
                        label: '流程图',
                        children: (
                            <Skeleton loading={true}>
                                <></>
                            </Skeleton>
                        ),
                    }
                ]}
            />
        );
    }


    const items = [
        {
            key: 'flow',
            label: '流程详情',
            children: (
                <FlowDetail
                    flowData={flowData}
                    adviceForm={props.adviceForm}
                    form={props.form}
                    visible={props.visible}
                    view={props.view}/>
            ),
        },
        flowData.showHistory() && {
            key: 'history',
            label: '流程记录',
            children: <FlowHistory flowData={flowData}/>,
        },
        {
            key: 'chart',
            label: '流程图',
            children: <FlowChart flowData={flowData}/>,
        }
    ] as any[];

    return (
        <Tabs
            className="view-flow-tabs"
            items={items}
        />
    )
}

export default FlowTabs;

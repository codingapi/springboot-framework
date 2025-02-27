import React from "react";
import {FlowData} from "@/components/flow/flow/data";
import {Skeleton, Tabs} from "antd";
import {CustomButtonType, FlowFormView, FlowFormViewProps} from "@/components/flow/flow/types";
import {FormInstance} from "antd/es/form/hooks/useForm";
import FlowHistory from "@/components/flow/flow/FlowHistory";
import FlowChart from "@/components/flow/flow/FlowChart";
import FlowDetail from "@/components/flow/flow/FlowDetail";
import {useSelector} from "react-redux";
import {FlowReduxState} from "@/components/flow/store/FlowSlice";


interface FlowTabsProps {
    flowData: FlowData;
    view: React.ComponentType<FlowFormViewProps> | FlowFormView;
    form: FormInstance<any>;
    adviceForm: FormInstance<any>;
    // 预览模式
    review?: boolean;
    // 请求数据加载
    requestLoading: boolean;
    // 设置请求数据加载状态
    setRequestLoading: (loading: boolean) => void;

    // 流程交互操作
    handlerClick: (data: {
        type: CustomButtonType;
        id?: string;
    }) => void;
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
                    handlerClick={props.handlerClick}
                    flowData={flowData}
                    adviceForm={props.adviceForm}
                    form={props.form}
                    requestLoading={props.requestLoading}
                    setRequestLoading={props.setRequestLoading}
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

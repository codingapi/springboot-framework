import React from "react";
import {Provider} from "react-redux";
import {flowStore} from "@/components/flow/store/FlowSlice";
import {FlowFormViewProps, FlowViewProps} from "@/components/flow/types";
import {Tabs} from "antd-mobile";
import {FlowViewContext} from "@/components/flow/data";
import FlowFooter from "@/components/flow/components/FlowFooter";
import "./index.scss";

const $FlowView: React.FC<FlowViewProps> = (props) => {

    const flowViewContext = new FlowViewContext(props);

    const FlowFormView = flowViewContext.getFlowFormView() as React.ComponentType<FlowFormViewProps>;

    const buttons = [
        {
            title: '提交',
            color: 'primary',
            onClick: () => {
                console.log('提交')
            }
        },
        {
            title: '提交给人事管理员',
            color: 'default',
            onClick: () => {
                console.log('暂存1')
            }
        },
        {
            title: '暂存2',
            color: 'default',
            onClick: () => {
                console.log('暂存1')
            }
        },
        {
            title: '暂存3',
            color: 'default',
            onClick: () => {
                console.log('暂存1')
            }
        },
        {
            title: '暂存4',
            color: 'default',
            onClick: () => {
                console.log('暂存1')
            }
        },
        {
            title: '暂存5',
            color: 'default',
            onClick: () => {
                console.log('暂存1')
            }
        },
        {
            title: '暂存6',
            color: 'default',
            onClick: () => {
                console.log('暂存1')
            }
        },
    ]

    return (
        <div className={"flow-view"}>
            <div className={"flow-view-content"}>
                <Tabs>
                    <Tabs.Tab title='详情' key='detail'>
                        <FlowFormView/>
                    </Tabs.Tab>
                    <Tabs.Tab title='流程' key='flow'>
                        流程详情
                    </Tabs.Tab>
                </Tabs>
            </div>
            <FlowFooter buttons={buttons}/>
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

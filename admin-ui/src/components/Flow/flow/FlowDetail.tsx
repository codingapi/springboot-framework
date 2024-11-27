import React from "react";
import {Divider, Result} from "antd";
import {ProForm, ProFormTextArea} from "@ant-design/pro-components";
import {FlowFormView, FlowFormViewProps} from "@/components/Flow/flow/types";
import {FlowData} from "@/components/Flow/flow/data";

interface FlowDetailProps {
    view: React.ComponentType<FlowFormViewProps> | FlowFormView;
    visible: boolean;
    form: any;
    adviceForm: any;
    review?: boolean;
    flowData: FlowData;
}

const FlowDetail: React.FC<FlowDetailProps> = (props) => {
    const flowData = props.flowData;

    const FlowFormView = flowData.getFlowFormView(props.view) as React.ComponentType<FlowFormViewProps>;

    return (
        <>
            {FlowFormView && (
                <FlowFormView
                    data={flowData.getFlowData()}
                    form={props.form}
                    visible={props.visible}
                    editable={flowData.getFlowNodeEditable()}
                    compare={!flowData.isStartFlow()}
                />
            )}

            {!FlowFormView && (
                <Result
                    status="404"
                    title="未设置流程视图"
                    subTitle="抱歉，该流程未设置流程视图，无法正常展示"
                />
            )}

            {/*仅当非发起流程时再展示审批意见框*/}
            {FlowFormView && !flowData.isStartFlow() && (
                <div className="opinionForm">
                    <div>
                        <Divider>
                            审批意见
                        </Divider>
                        <ProForm
                            form={props.adviceForm}
                            submitter={false}
                            autoFocusFirstInput={false}
                        >
                            <ProFormTextArea
                                disabled={props.review}
                                label={""}
                                placeholder={'请输入审批意见'}
                                name={"advice"}
                            />
                        </ProForm>
                    </div>
                </div>
            )}
        </>
    )
}

export default FlowDetail;

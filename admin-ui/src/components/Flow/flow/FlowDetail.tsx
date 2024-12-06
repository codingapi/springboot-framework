import React from "react";
import {Divider, Result} from "antd";
import {ProForm, ProFormTextArea} from "@ant-design/pro-components";
import {CustomButtonType, FlowFormView, FlowFormViewProps} from "@/components/Flow/flow/types";
import {FlowData} from "@/components/Flow/flow/data";
import {useDispatch, useSelector} from "react-redux";
import {
    clearTriggerClick,
    FlowReduxState,
    hideOpinionEditor,
    showOpinionEditor
} from "@/components/Flow/store/FlowSlice";

interface FlowDetailProps {
    view: React.ComponentType<FlowFormViewProps> | FlowFormView;
    visible: boolean;
    form: any;
    adviceForm: any;
    review?: boolean;
    flowData: FlowData;
    // 流程交互操作
    handlerClick: (data: {
        type: CustomButtonType;
        id?: string;
    }) => void;
}

const FlowDetail: React.FC<FlowDetailProps> = (props) => {
    const flowData = props.flowData;

    const FlowFormView = flowData.getFlowFormView(props.view) as React.ComponentType<FlowFormViewProps>;

    // 触发点击事件
    const triggerClickVisible = useSelector((state: FlowReduxState) => state.flow.triggerClickVisible);

    // 审批意见输入框
    const opinionEditorVisible = useSelector((state: FlowReduxState) => state.flow.opinionEditorVisible);

    // flow store redux
    const dispatch = useDispatch();

    return (
        <>
            <div className="flowApprovalViewBox">
                {FlowFormView && (
                    <div className="flowViewDetail"
                         style={{height: !FlowFormView || flowData.isStartFlow() ? '85vh' : '68vh'}}>
                        <FlowFormView
                            handlerClick={props.handlerClick}
                            data={flowData.getFlowData()}
                            form={props.form}
                            flowData={flowData}
                            visible={props.visible}
                            editable={!flowData.isDone() && flowData.getFlowNodeEditable()}
                            compare={!flowData.isStartFlow()}
                            triggerClickVisible={triggerClickVisible}
                            opinionEditorVisible={(visible) => {
                                if (visible) {
                                    dispatch(showOpinionEditor());
                                } else {
                                    dispatch(hideOpinionEditor());
                                }
                            }}
                            clearTriggerClick={() => {
                                dispatch(clearTriggerClick());
                            }}
                        />
                    </div>
                )}

                {!FlowFormView && (
                    <Result
                        status="404"
                        title="未设置流程视图"
                        subTitle="抱歉，该流程未设置流程视图，无法正常展示"
                    />
                )}

                {/*仅当非发起流程时再展示审批意见框*/}
                {FlowFormView && flowData.showOpinion() && opinionEditorVisible && (
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
            </div>
        </>
    )
}

export default FlowDetail;

import React, {useEffect} from "react";
import {Divider, Result} from "antd";
import {ProForm, ProFormTextArea} from "@ant-design/pro-components";
import {CustomButtonType, FlowFormView, FlowFormViewProps} from "@/components/Flow/flow/types";
import {FlowData} from "@/components/Flow/flow/data";
import {useDispatch, useSelector} from "react-redux";
import {FlowReduxState, hideOpinionEditor, showOpinionEditor} from "@/components/Flow/store/FlowSlice";
import {FormInstance} from "antd/es/form/hooks/useForm";

interface FlowDetailProps {
    view: React.ComponentType<FlowFormViewProps> | FlowFormView;
    form: FormInstance<any>;
    adviceForm: FormInstance<any>;
    review?: boolean;
    flowData: FlowData;
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

const FlowDetail: React.FC<FlowDetailProps> = (props) => {
    const flowData = props.flowData;

    const FlowFormView = flowData.getFlowFormView(props.view) as React.ComponentType<FlowFormViewProps>;

    // 触发点击事件Key
    const eventKey = useSelector((state: FlowReduxState) => state.flow.eventKey);

    // 审批意见输入框
    const opinionEditorVisible = useSelector((state: FlowReduxState) => state.flow.opinionEditorVisible);

    // 流程视图内容
    const flowViewVisible = useSelector((state: FlowReduxState) => state.flow.flowViewVisible);

    // flow store redux
    const dispatch = useDispatch();

    useEffect(() => {
        if (flowViewVisible) {
            const advice = flowData.getOpinionAdvice();
            props.adviceForm.setFieldsValue({
                advice: advice
            });
        }
    }, [flowViewVisible]);

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
                            visible={flowViewVisible}
                            opinions={flowData.getOpinions()}
                            editable={!flowData.isDone() && flowData.getFlowNodeEditable()}
                            compare={!flowData.isStartFlow()}
                            eventKey={eventKey}
                            requestLoading={props.requestLoading}
                            setRequestLoading={props.setRequestLoading}
                            opinionEditorVisible={(visible) => {
                                if (visible) {
                                    dispatch(showOpinionEditor());
                                } else {
                                    dispatch(hideOpinionEditor());
                                }
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
                {FlowFormView && opinionEditorVisible && (
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

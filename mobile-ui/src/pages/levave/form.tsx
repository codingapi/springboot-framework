import React, {useContext, useEffect} from "react";
import Form from "@/components/form";
import {FormField} from "@/components/form/types";
import {FlowFormViewProps} from "@/components/flow/types";
import {Button, Toast} from "antd-mobile";
import {FlowViewReactContext} from "@/components/flow/view";

const LeaveForm: React.FC<FlowFormViewProps> = (props) => {
    const formAction = props.formAction;
    const flowViewReactContext = useContext(FlowViewReactContext);

    console.log('LeaveForm init:', props);

    useEffect(() => {
        // 添加自定义事件触发器
        flowViewReactContext?.flowTriggerContext.addTrigger((eventKey) => {
            console.log('eventKey:', eventKey);
        });
    }, [flowViewReactContext]);


    useEffect(() => {
        // 设置表单数据
        if(props.data){
            formAction.current?.setFieldsValue({
                ...props.data
            });
        }
    }, [props.data]);

    const flowButtons = flowViewReactContext?.flowRecordContext?.getFlowButtons();

    return (
        <Form
            actionRef={formAction}
            loadFields={async () => {
                return [
                    {
                        type: "input",
                        props: {
                            name: "clazzName",
                            hidden: true
                        }
                    },
                    {
                        type: "input",
                        props: {
                            name: "username",
                            hidden: true
                        }
                    },
                    {
                        type: "input",
                        props: {
                            label: "请假天数",
                            name: "days",
                            required: true,
                            validateFunction: async (content) => {
                                if (content.value <= 0) {
                                    return ["请假天数不能小于0"];
                                }
                                return []
                            }
                        }
                    },
                    {
                        type: "textarea",
                        props: {
                            label: "请假理由",
                            name: "desc",
                            required: true,
                            validateFunction: async (content) => {
                                if (content.value && content.value.length > 0) {
                                    return []
                                }
                                return ["请假理由不能为空"];
                            }
                        }
                    }
                ] as FormField[]
            }}
            onFinish={async (values) => {
                console.log('values:', values);
            }}
            footer={(
                <div
                    style={{
                        display: 'grid',
                        gridTemplateColumns: '1fr 1fr',
                    }}
                >
                    <Button
                        style={{
                            margin: 5
                        }}
                        onClick={async () => {
                            formAction.current && await formAction.current.validate();
                        }}
                    >校验表单</Button>

                    <Button
                        style={{
                            margin: 5
                        }}
                        onClick={async () => {
                            const recordId = flowViewReactContext?.flowStateContext?.getRecordId() || "";
                            Toast.show(`流程编号：${recordId}`);
                        }}
                    >获取流程编号</Button>

                    <Button
                        style={{
                            margin: 5
                        }}
                        onClick={async () => {
                            const isApproval = flowViewReactContext?.flowRecordContext?.isApproval();
                            Toast.show(`是否可以审批：${isApproval ? '是' : '否'}`);
                        }}
                    >是否可审批</Button>

                    <Button
                        style={{
                            margin: 5
                        }}
                        onClick={async () => {
                            const isStartNode = flowViewReactContext?.flowRecordContext?.isStartNode();
                            Toast.show(`是否开始节点：${isStartNode ? '是' : '否'}`);
                        }}
                    >是否开始节点</Button>

                    <Button
                        style={{
                            margin: 5
                        }}
                        onClick={async () => {
                            const nodeCode = flowViewReactContext?.flowRecordContext?.getNodeCode();
                            Toast.show(`当前流程编码：${nodeCode}`);
                        }}
                    >当前流程编码</Button>

                    <Button
                        style={{
                            margin: 5
                        }}
                        onClick={async () => {
                            const isEditable = flowViewReactContext?.flowRecordContext?.isEditable();
                            Toast.show(`是否可编辑：${isEditable ? '是' : '否'}`);
                        }}
                    >是否可编辑</Button>

                    <Button
                        style={{
                            margin: 5
                        }}
                        onClick={async () => {
                            const isDone = flowViewReactContext?.flowRecordContext?.isDone();
                            Toast.show(`是否已审批：${isDone ? '是' : '否'}`);
                        }}
                    >是否已审批</Button>

                    <Button
                        style={{
                            margin: 5
                        }}
                        onClick={async () => {
                            const isFinished = flowViewReactContext?.flowRecordContext?.isFinished();
                            Toast.show(`是否完成：${isFinished ? '是' : '否'}`);
                        }}
                    >是否完成</Button>


                    <Button
                        style={{
                            margin: 5
                        }}
                        onClick={async () => {
                           flowViewReactContext?.flowEventContext.reloadFlow();
                        }}
                    >刷新数据</Button>

                    {flowButtons && flowButtons.map((button, index) => {
                        return (
                            <Button
                                style={{
                                    margin: 5
                                }}
                                onClick={()=>{
                                    flowViewReactContext?.flowButtonClickContext?.handlerClick(button);
                                }}
                            >
                                {button.name}
                            </Button>
                        )
                    })}
                </div>
            )}
        />
    );
}

export default LeaveForm;

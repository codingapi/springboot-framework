import React, {useContext, useEffect} from "react";
import {Form} from "@codingapi/form-mobile";
import {FlowFormViewProps} from "@codingapi/ui-framework";
import {Button, Toast} from "antd-mobile";
import {FlowViewReactContext} from "@codingapi/flow-mobile";
import {fields} from "@/pages/levave/fields";

const LeaveForm: React.FC<FlowFormViewProps> = (props) => {
    const formInstance = props.form;
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
        if (props.dataVersion && props.data) {
            formInstance?.setFieldsValue({
                ...props.data
            });
        }
    }, [props.dataVersion]);

    const flowButtons = flowViewReactContext?.flowRecordContext?.getFlowButtons();

    return (
        <Form
            form={formInstance}
            loadFields={async () => {
                return fields;
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
                            formInstance?.validate();
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
                                key={index}
                                style={{
                                    margin: 5
                                }}
                                onClick={() => {
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

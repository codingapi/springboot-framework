import React, {useEffect} from "react";
import {ProForm, ProFormDigit, ProFormText, ProFormTextArea} from "@ant-design/pro-components";
import {FlowFormViewProps} from "@/components/Flow/flow/types";
import {Button} from "antd";
import {useSelector} from "react-redux";
import {FlowReduxState} from "@/components/Flow/store/FlowSlice";


const LeaveForm: React.FC<FlowFormViewProps> = (props) => {

    // 审批意见输入框展示状态
    const opinionEditorVisible = useSelector((state: FlowReduxState) => state.flow.opinionEditorVisible);

    useEffect(() => {
        props.form.setFieldsValue(props.data);
        // 关闭意见输入框
        props.opinionEditorVisible && props.opinionEditorVisible(false);
    }, []);

    const triggerClickVisible = props.triggerClickVisible;

    return (
        <ProForm
            form={props.form}
            submitter={false}
        >

            <ProFormText
                name={"id"}
                hidden={true}
            />

            <ProFormText
                name={"username"}
                hidden={true}
            />

            <ProFormDigit
                name={"days"}
                label={"请假天数"}
                fieldProps={{
                    step: 1
                }}
                rules={[
                    {
                        required: true,
                        message: "请输入请假天数"
                    }
                ]}
            />

            <ProFormTextArea
                name={"desc"}
                label={"请假原因"}
                rules={[
                    {
                        required: true,
                        message: "请输入请假原因"
                    }
                ]}
            />

            <Button
                onClick={()=>{
                    props.opinionEditorVisible && props.opinionEditorVisible(!opinionEditorVisible);
                }}
            >
                意见输入框
            </Button>


            {triggerClickVisible && (
                <Button
                    onClick={()=>{
                        props.clearTriggerClick && props.clearTriggerClick();
                    }}
                >点击了自定义事件</Button>
            )}

        </ProForm>
    )
}

export default LeaveForm;

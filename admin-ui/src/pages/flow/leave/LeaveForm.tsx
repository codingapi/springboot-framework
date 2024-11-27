import React, {useEffect} from "react";
import {ProForm, ProFormDigit, ProFormText, ProFormTextArea} from "@ant-design/pro-components";
import {FlowFormViewProps} from "@/components/Flow/flow/types";


const LeaveForm: React.FC<FlowFormViewProps> = (props) => {

    useEffect(() => {
        props.form.setFieldsValue(props.data);
    }, []);


    return (
        <ProForm
            form={props.form}
            submitter={false}
        >

            <ProFormText
                name={"id"}
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
        </ProForm>
    )
}

export default LeaveForm;

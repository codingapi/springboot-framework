import React from "react";
import Form from "@/components/form";
import {FormField} from "@/components/form/types";
import {FlowFormViewProps} from "@/components/flow/types";
import {Button} from "antd-mobile";

const LeaveForm: React.FC<FlowFormViewProps> = (props) => {

    const formAction = props.formAction;

    return (
        <Form
            initialValues={{
                ...props.data
            }}
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
                            validateFunction:async (content)=>{
                                if(content.value<=0){
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
                            validateFunction:async (content)=>{
                                if(content.value && content.value.length>0){
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
                <div>
                    <Button
                        onClick={async () => {
                            formAction.current && await formAction.current.validate();
                        }}
                    >test</Button>
                </div>
            )}
        />
    );
}

export default LeaveForm;

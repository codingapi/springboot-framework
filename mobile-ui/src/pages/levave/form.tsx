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
                            rules: [
                                {
                                    required: true,
                                    message: '请输入请假天数'
                                }
                            ]
                        }
                    },
                    {
                        type: "textarea",
                        props: {
                            label: "请假理由",
                            name: "desc",
                            required: true,
                            rules: [
                                {
                                    required: true,
                                    message: '请输入请假理由'
                                }
                            ]
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
                        onClick={() => {
                            formAction.current && formAction.current.submit();
                        }}
                    >test</Button>
                </div>
            )}
        />
    );
}

export default LeaveForm;

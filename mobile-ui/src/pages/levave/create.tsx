import React from "react";
import Header from "@/layout/Header";
import Form from "@/components/form";
import FormInput from "@/components/form/input";
import FormTextArea from "@/components/form/textarea";
import {Button} from "antd-mobile";

const LeaveCreatePage = () => {

    return (
        <div>
            <Header>发起请假</Header>

            <div
                style={{
                    marginTop: 5
                }}
            >
                <Form
                    footer={(
                        <div>
                            <Button
                                color={"primary"}
                                block={true}
                            >发起请假</Button>
                        </div>
                    )}
                    initialValues={{
                        days: 1
                    }}
                >
                    <FormInput
                        inputType={"number"}
                        label={"请假天数"}
                        name={"days"}
                        required={true}
                        rules={[
                            {
                                required: true,
                                message: '请输入请假天数'
                            }
                        ]}
                    />

                    <FormTextArea
                        label={"请假理由"}
                        name={"desc"}
                        required={true}
                        rules={[
                            {
                                required: true,
                                message: '请输入请假理由'
                            }
                        ]}
                    />

                </Form>
            </div>
        </div>
    )
}

export default LeaveCreatePage;

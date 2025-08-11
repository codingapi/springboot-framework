import React, {useEffect} from "react";
import {FlowFormViewProps} from "@codingapi/ui-framework";
import {Form, FormItem} from "@codingapi/form-pc";

const LeaveForm: React.FC<FlowFormViewProps> = (props) => {

    useEffect(() => {
        if (props.dataVersion && props.data) {
            console.log('data', props.data);
            props.form?.setFieldsValue({
                ...props.data
            });
        }
    }, [props.dataVersion]);

    return (
        <Form
            form={props.form}
            layout={"vertical"}
        >

            <FormItem
                name={"id"}
                hidden={true}
                type={"input"}
            />

            <FormItem
                type={"input"}
                name={"username"}
                hidden={true}
            />

            <FormItem
                type={"input"}
                name={"days"}
                label={"请假天数"}
                inputType={"number"}
                required={true}
                rules={[
                    {
                        required: true,
                        message: "请假天数不能为空",
                    },
                ]}
            />

            <FormItem
                type={"textarea"}
                name={"desc"}
                label={"请假原因"}
                required={true}
                rules={[
                    {
                        required: true,
                        message: "请假原因不能为空",
                    },
                ]}
            />

        </Form>
    )
}

export default LeaveForm;

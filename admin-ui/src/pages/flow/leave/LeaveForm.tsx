import React, {useEffect} from "react";
import {FlowFormViewProps, ValidateUtils} from "@codingapi/ui-framework";
import {Form, FormInput, FormTextArea} from "@codingapi/form-pc";

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

            <FormInput
                name={"id"}
                hidden={true}
            />

            <FormInput
                name={"username"}
                hidden={true}
            />

            <FormInput
                name={"days"}
                label={"请假天数"}
                inputType={"number"}
                required={true}
                validateFunction={ValidateUtils.validateNotEmpty}
            />

            <FormTextArea
                name={"desc"}
                label={"请假原因"}
                required={true}
                validateFunction={ValidateUtils.validateNotEmpty}
            />

        </Form>
    )
}

export default LeaveForm;

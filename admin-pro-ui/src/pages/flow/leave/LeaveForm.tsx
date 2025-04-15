import React, {useEffect} from "react";
import {FlowFormViewProps} from "@/components/flow/types";
import Form from "@/components/form";
import FormInput from "@/components/form/input";
import ValidateUtils from "@/components/form/utils";
import FormTextArea from "@/components/form/textarea";

const LeaveForm: React.FC<FlowFormViewProps> = (props) => {

    useEffect(() => {
        if (props.dataVersion && props.data) {
            props.form?.setFieldsValue({
                ...props.data
            });
        }
    }, [props.dataVersion]);

    return (
        <Form
            form={props.form}
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

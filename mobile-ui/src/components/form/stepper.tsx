import React from "react";
import {FormItemProps} from "@/components/form/types";
import {Form, Stepper} from "antd-mobile";
import formFieldInit from "@/components/form/common";

const FormStepper: React.FC<FormItemProps> = (props) => {

    const {formAction,rules,validateContext} = formFieldInit(props);

    return (
        <Form.Item
            name={props.name}
            label={props.label}
            rules={rules}
            hidden={props.hidden}
            help={props.help}
            disabled={props.disabled}
        >
            <Stepper
                value={props.value}
                max={props.stepperMaxNumber}
                min={props.stepperMinNumber}
                digits={props.stepperDecimalLength}
                onChange={(e) => {
                    if(formAction) {
                        validateContext?.validateField(props.name, formAction);
                    }
                    props.onChange && props.onChange(e, formAction);
                }}
            />
        </Form.Item>
    )
}

export default FormStepper;

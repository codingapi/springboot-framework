import React from "react";
import {FormItemProps} from "@/components/form/types";
import {Form, Input} from "antd";
import formFieldInit from "@/components/form/common";

const FormPassword: React.FC<FormItemProps> = (props) => {

    const {formAction} = formFieldInit(props);

    return (
        <Form.Item
            name={props.name}
            label={props.label}
            hidden={props.hidden}
            help={props.help}
            required={props.required}
        >
            <Input.Password
                disabled={props.disabled}
                value={props.value}
                placeholder={props.placeholder}
                onChange={(value) => {
                    const currentValue = value.target.value;
                    formAction?.setFieldValue(props.name, currentValue);
                    props.onChange && props.onChange(currentValue, formAction);
                }}
            />
        </Form.Item>
    )
}

export default FormPassword;

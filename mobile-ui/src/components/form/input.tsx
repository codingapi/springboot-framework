import React from "react";
import {FormItemProps} from "@/components/form/types";
import {Form, Input} from "antd-mobile";
import formFieldInit from "@/components/form/common";

const FormInput: React.FC<FormItemProps> = (props) => {

    const inputType = props.inputType || "text";
    const {formAction, rules} = formFieldInit(props);

    return (
        <Form.Item
            name={props.name}
            label={props.label}
            rules={rules}
            hidden={props.hidden}
            help={props.help}
            disabled={props.disabled}
        >
            <Input
                value={props.value}
                clearable={true}
                type={inputType}
                placeholder={props.placeholder}
                maxLength={props.inputMaxLength}
                onChange={(value) => {
                    formAction?.setFieldValue(props.name, value);
                    props.onChange && props.onChange(value, formAction);
                }}
            />
        </Form.Item>
    )
}

export default FormInput;

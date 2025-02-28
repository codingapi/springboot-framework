import React from "react";
import {FormItemProps} from "@/components/form/types";
import {Form, Input} from "antd";
import formFieldInit from "@/components/form/common";
import "./form.scss";

const FormInput: React.FC<FormItemProps> = (props) => {

    const inputType = props.inputType || "text";
    const {formAction} = formFieldInit(props);

    return (
        <Form.Item
            name={props.name}
            label={props.label}
            hidden={props.hidden}
            help={props.help}
            required={props.required}
        >
            <Input
                disabled={props.disabled}
                value={props.value}
                type={inputType}
                placeholder={props.placeholder}
                maxLength={props.inputMaxLength}
                addonAfter={props.addonAfter}
                addonBefore={props.addonBefore}
                onChange={(value) => {
                    const currentValue = value.target.value;
                    formAction?.setFieldValue(props.name, currentValue);
                    props.onChange && props.onChange(currentValue, formAction);
                }}
            />
        </Form.Item>
    )
}

export default FormInput;

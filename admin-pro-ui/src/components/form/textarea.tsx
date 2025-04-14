import React from "react";
import {FormItemProps} from "@/components/form/types";
import {Form, Input} from "antd";
import formFieldInit from "@/components/form/common";
import "./form.scss";

const FormTextArea: React.FC<FormItemProps> = (props) => {

    const {formContext} = formFieldInit(props);

    return (
        <Form.Item
            name={props.name}
            label={props.label}
            required={props.required}
            hidden={props.hidden}
            help={props.help}
            tooltip={props.tooltip}
        >
            <Input.TextArea
                disabled={props.disabled}
                value={props.value}
                showCount={true}
                placeholder={props.placeholder}
                maxLength={props.textAreaMaxLength}
                rows={props.textAreaRows}
                onChange={(value) => {
                    const currentValue = value.target.value;
                    formContext?.setFieldValue(props.name, currentValue);
                    props.onChange && props.onChange(currentValue, formContext);
                }}
            />
        </Form.Item>
    )
}

export default FormTextArea;

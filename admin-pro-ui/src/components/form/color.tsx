import React from "react";
import {FormItemProps} from "@/components/form/types";
import {ColorPicker, Form} from "antd";
import formFieldInit from "@/components/form/common";
import "./form.scss";
import type {AggregationColor} from "antd/es/color-picker/color";

const formToValue = (value: AggregationColor) => {
    if (value) {
        return value.toHexString();
    }
    return value;
}

const FormColor: React.FC<FormItemProps> = (props) => {

    const {formAction} = formFieldInit(props);

    return (
        <Form.Item
            name={props.name}
            label={props.label}
            hidden={props.hidden}
            help={props.help}
            required={props.required}
        >
            <ColorPicker
                disabled={props.disabled}
                value={props.value}
                onChange={(value) => {
                    const currentValue = formToValue(value);
                    formAction?.setFieldValue(props.name, currentValue);
                    props.onChange && props.onChange(currentValue, formAction);
                }}
            />
        </Form.Item>
    )
}

export default FormColor;

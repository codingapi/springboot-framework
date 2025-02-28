import React from "react";
import {FormItemProps} from "@/components/form/types";
import {ColorPicker, Form, Space} from "antd";
import formFieldInit from "@/components/form/common";
import "./form.scss";
import type {AggregationColor} from "antd/es/color-picker/color";
import {FormAction} from "@/components/form/index";

const formToValue = (value: AggregationColor) => {
    if (value) {
        return value.toHexString();
    }
    return value;
}

interface $ColorPickerProps extends FormItemProps{
    formAction?:FormAction;
}

const $ColorPicker:React.FC<$ColorPickerProps> = (props)=>{
    const formAction = props.formAction;

    return (
       <Space.Compact
           style={{
               width:"100%"
           }}
       >
           {props.addonBefore}
           <ColorPicker
               disabled={props.disabled}
               value={props.value}
               onChange={(value) => {
                   const currentValue = formToValue(value);
                   formAction?.setFieldValue(props.name, currentValue);
                   props.onChange && props.onChange(currentValue, formAction);
               }}
           />
           {props.addonAfter}
       </Space.Compact>
    )
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
            tooltip={props.tooltip}
        >
            <$ColorPicker
                {...props}
                formAction={formAction}
            />

        </Form.Item>
    )
}

export default FormColor;

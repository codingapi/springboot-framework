import React from "react";
import {FormItemProps} from "@/components/form/types";
import {Form, Rate} from "antd";
import formFieldInit from "@/components/form/common";
import "./form.scss";

const FormRate: React.FC<FormItemProps> = (props) => {
    const {formContext} = formFieldInit(props);

    return (
        <Form.Item
            name={props.name}
            label={props.label}
            hidden={props.hidden}
            help={props.help}
            required={props.required}
            tooltip={props.tooltip}
        >
            <Rate
                disabled={props.disabled}
                count={props.rateCount}
                allowHalf={props.rateAllowHalf}
                value={props.value}
                onChange={(value) => {
                    formContext?.setFieldValue(props.name, value);
                    props.onChange && props.onChange(value, formContext);
                }}
            />
        </Form.Item>
    )
}

export default FormRate;

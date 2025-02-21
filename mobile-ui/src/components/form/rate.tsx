import React from "react";
import {FormItemProps} from "@/components/form/types";
import {Form, Rate} from "antd-mobile";
import formFieldInit from "@/components/form/common";

const FormRate: React.FC<FormItemProps> = (props) => {
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
            <Rate
                count={props.rateCount}
                allowHalf={props.rateAllowHalf}
                value={props.value}
                onChange={(value) => {
                    formAction?.setFieldValue(props.name, value);
                    props.onChange && props.onChange(value, formAction);
                }}
            />
        </Form.Item>
    )
}

export default FormRate;

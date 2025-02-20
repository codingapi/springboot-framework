import React from "react";
import {FormItemProps} from "@/components/form/types";
import {Form, Rate} from "antd-mobile";
import formFieldInit from "@/components/form/common";

const FormRate: React.FC<FormItemProps> = (props) => {
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
            <Rate
                count={props.rateCount}
                allowHalf={props.rateAllowHalf}
                value={props.value}
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

export default FormRate;

import React from "react";
import {FormItemProps} from "@/components/form/types";
import {Form, TextArea} from "antd-mobile";
import formFieldInit from "@/components/form/common";
import "./form.scss";

const FormTextArea: React.FC<FormItemProps> = (props) => {

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
            <TextArea
                value={props.value}
                showCount={true}
                placeholder={props.placeholder}
                maxLength={props.textAreaMaxLength}
                rows={props.textAreaRows}
                onChange={(value) => {
                    formAction?.setFieldValue(props.name, value);
                    props.onChange && props.onChange(value, formAction);
                }}
            />
        </Form.Item>
    )
}

export default FormTextArea;

import React from "react";
import {FormItemProps} from "@/components/form/types";
import {Form, Switch as AntSwitch} from "antd-mobile";
import {SwitchProps as AntdSwitchProps} from "antd-mobile/es/components/switch/switch";
import formFieldInit from "@/components/form/common";

interface SwitchProps extends AntdSwitchProps {
    value?: boolean;
}

const Switch: React.FC<SwitchProps> = ({value, ...props}) => {
    return (
        <AntSwitch checked={value} {...props}/>
    )
}

const FormSwitch: React.FC<FormItemProps> = (props) => {

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
            <Switch
                value={props.value}
                checkedText={props.switchCheckText}
                uncheckedText={props.switchUnCheckText}
                onChange={(value) => {
                    formAction?.setFieldValue(props.name, value);
                    props.onChange && props.onChange(value, formAction);
                }}
            />
        </Form.Item>
    )
}

export default FormSwitch;

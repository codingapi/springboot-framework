import React, {useEffect} from "react";
import {FormItemProps} from "@/components/form/types";
import {Button, Form, Select, Space} from "antd";
import formFieldInit from "@/components/form/common";
import "./form.scss";
import {FormAction} from "@/components/form/index";

const valueToForm = (value: string) => {
    if (value && value.length > 0) {
        return value.split(",");
    }
    return value;
}

const formToValue = (value: string[] |string) => {
    if(value instanceof Array) {
        if (value && value.length > 0) {
            return value.join(",")
        }
    }
    return value;
}

interface $SelectProps extends FormItemProps{
    formAction?:FormAction;
}

const $Select: React.FC<$SelectProps> = (props) => {
    const formAction = props.formAction;

    return (
        <Space.Compact
            style={{
                width:"100%"
            }}
        >
            {props.addonBefore}
            <Select
                prefix={props.prefix}
                suffixIcon={props.suffix}
                disabled={props.disabled}
                value={props.value}
                mode={props.selectMultiple ? "multiple" : undefined}
                placeholder={props.placeholder}
                showSearch={true}
                options={props.options}
                onChange={(value,option) => {
                    formAction?.setFieldValue(props.name, formToValue(value as string[]));
                    props.onChange && props.onChange(value, formAction);
                }}
            />
            {props.addonAfter}
        </Space.Compact>
    )
}

const FormSelect: React.FC<FormItemProps> = (props) => {

    const [options, setOptions] = React.useState(props.options);

    const {formAction} = formFieldInit(props, () => {
        reloadOptions();
    });

    const reloadOptions = () => {
        if (props.loadOptions) {
            props.loadOptions(formAction).then(list => {
                setOptions(list);
            });
        }
    }

    useEffect(() => {
        reloadOptions();
    }, []);

    return (
        <Form.Item
            name={props.name}
            label={props.label}
            required={props.required}
            hidden={props.hidden}
            help={props.help}
            tooltip={props.tooltip}
            getValueProps={(value) => {
                if (value) {
                    return {
                        value: valueToForm(value)
                    }
                }
                return value
            }}
        >
            <$Select
                {...props}
                options={options}
                formAction={formAction}
            />

        </Form.Item>
    )
}

export default FormSelect;

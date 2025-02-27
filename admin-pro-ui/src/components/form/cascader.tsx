import React, {useEffect} from "react";
import {FormItemProps} from "@/components/form/types";
import {Cascader, Form} from "antd";
import formFieldInit from "@/components/form/common";
import "./form.scss";

const valueToForm = (value: string) => {
    if (value && value.length > 0) {
        return value.split(",");
    }
    return value;
}

const formToValue = (value: string[]) => {
    if (value && value.length > 0) {
        return value.join(",")
    }
    return value;
}

const FormCascader: React.FC<FormItemProps> = (props) => {

    const [visible, setVisible] = React.useState(false);
    const [options, setOptions] = React.useState(props.options);

    const {formAction} = formFieldInit(props, () => {
        reloadOptions();
    });

    const reloadOptions = () => {
        if (props.loadOptions) {
            props.loadOptions(formAction).then(res => {
                setOptions(res);
            });
        }
    }

    useEffect(() => {
        reloadOptions();
    }, []);

    useEffect(() => {
        if (visible) {
            reloadOptions();
        }
    }, [visible]);

    return (
        <Form.Item
            name={props.name}
            label={props.label}
            required={props.required}
            hidden={props.hidden}
            help={props.help}

            getValueProps={(value) => {
                if (value) {
                    return {
                        value: valueToForm(value)
                    }
                }
                return value
            }}
        >
            <Cascader
                disabled={props.disabled}
                value={props.value}
                options={options || []}
                onChange={(value) => {
                    formAction?.setFieldValue(props.name, formToValue(value as string[]));
                    props.onChange && props.onChange(value, formAction);
                    setVisible(false);
                }}
            >
            </Cascader>
        </Form.Item>
    )
}

export default FormCascader;

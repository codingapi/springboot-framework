import React, {useEffect} from "react";
import {FormItemProps} from "@/components/form/types";
import {Checkbox, Form, Space} from "antd-mobile";
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

const FormCheckbox: React.FC<FormItemProps> = (props) => {
    const [options, setOptions] = React.useState(props.options);

    const {formContext, rules} = formFieldInit(props, () => {
        reloadOptions();
    });

    const reloadOptions = () => {
        if (props.loadOptions) {
            props.loadOptions(formContext).then(res => {
                setOptions(res);
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
            rules={rules}
            hidden={props.hidden}
            help={props.help}
            disabled={props.disabled}
            getValueProps={(value) => {
                if (value) {
                    return {
                        value: valueToForm(value)
                    }
                }
                return value
            }}
        >
            <Checkbox.Group
                value={props.value}
                onChange={(e) => {
                    formContext?.setFieldValue(props.name, formToValue(e as string[]));
                    props.onChange && props.onChange(e, formContext)
                }}
            >
                <Space direction={props.checkboxDirection}>
                    {options?.map(item => {
                        return (
                            <Checkbox
                                disabled={item.disable}
                                value={item.value}
                            >{item.label}</Checkbox>
                        )
                    })}
                </Space>
            </Checkbox.Group>
        </Form.Item>
    )
}

export default FormCheckbox;

import React, {useEffect} from "react";
import {FormItemProps} from "@/components/form/types";
import {Form, Radio, Space} from "antd";
import formFieldInit from "@/components/form/common";

const FormRadio: React.FC<FormItemProps> = (props) => {
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

    return (
        <Form.Item
            name={props.name}
            label={props.label}
            hidden={props.hidden}
            help={props.help}
            required={props.required}
        >
            <Radio.Group
                disabled={props.disabled}
                value={props.value}
                onChange={(value) => {
                    const currentValue = value.target.value;
                    formAction?.setFieldValue(props.name, currentValue);
                    props.onChange && props.onChange(currentValue, formAction);
                }}
            >
                <Space direction={props.radioDirection}>
                    {options?.map(item => {
                        return (
                            <Radio
                                value={item.value}
                                disabled={item.disable}
                            >{item.label}</Radio>
                        )
                    })}
                </Space>
            </Radio.Group>
        </Form.Item>
    )
}

export default FormRadio;

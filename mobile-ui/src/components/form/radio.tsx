import React, {useEffect} from "react";
import {FormItemProps} from "@/components/form/types";
import {Form, Radio, Space} from "antd-mobile";
import "./form.scss";
import formFieldInit from "@/components/form/common";

const FormRadio: React.FC<FormItemProps> = (props) => {
    const [options, setOptions] = React.useState(props.options);

    const {formAction, rules} = formFieldInit(props, () => {
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
            rules={rules}
            hidden={props.hidden}
            help={props.help}
            disabled={props.disabled}
        >
            <Radio.Group
                value={props.value}
                onChange={(value) => {
                    formAction?.setFieldValue(props.name, value);
                    props.onChange && props.onChange(value, formAction);
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

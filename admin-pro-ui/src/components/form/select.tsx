import React, {useEffect} from "react";
import {FormItemProps} from "@/components/form/types";
import {Form, Select} from "antd";
import formFieldInit from "@/components/form/common";
import "./form.scss";

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
            getValueProps={(value) => {
                if (value) {
                    return {
                        value: valueToForm(value)
                    }
                }
                return value
            }}
        >
            <Select
                disabled={props.disabled}
                value={props.value}
                mode={props.selectMultiple ? "multiple" : undefined}
                placeholder={props.placeholder}
                showSearch={true}
                options={options}
                onChange={(value,option) => {
                    formAction?.setFieldValue(props.name, formToValue(value as string[]));
                    props.onChange && props.onChange(value, formAction);
                }}
            />
        </Form.Item>
    )
}

export default FormSelect;

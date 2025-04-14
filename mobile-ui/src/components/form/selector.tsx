import React, {useEffect} from "react";
import {FormItemProps} from "@/components/form/types";
import {Form, Selector} from "antd-mobile";
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


const FormSelector: React.FC<FormItemProps> = (props) => {

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
            <Selector
                multiple={props.selectorMultiple}
                columns={props.selectorColumn}
                options={options || []}
                value={props.value}
                onChange={(e) => {
                    formContext?.setFieldValue(props.name, formToValue(e));
                    props.onChange && props.onChange(e, formContext);
                }}
            />
        </Form.Item>
    )
}

export default FormSelector;

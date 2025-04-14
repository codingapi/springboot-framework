import React, {useEffect} from "react";
import {FormItemProps} from "@/components/form/types";
import {Cascader, Form, Space} from "antd";
import formFieldInit from "@/components/form/common";
import "./form.scss";
import FormInstance from "@/components/form/domain/FormInstance";

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

interface $CascaderProps extends FormItemProps{
    formInstance?:FormInstance;
}

const $Cascader:React.FC<$CascaderProps> = (props)=>{
    const formInstance = props.formInstance;
    return (
       <Space.Compact
           style={{
               width:"100%"
           }}
       >
           {props.addonBefore}
           <Cascader
               disabled={props.disabled}
               value={props.value}
               suffixIcon={props.suffix}
               prefix={props.prefix}
               options={props.options}
               onChange={(value) => {
                   formInstance?.setFieldValue(props.name, formToValue(value as string[]));
                   props.onChange && props.onChange(value, formInstance);
               }}
           />
           {props.addonAfter}
       </Space.Compact>
    )
}

const FormCascader: React.FC<FormItemProps> = (props) => {

    const [options, setOptions] = React.useState(props.options);

    const {formContext} = formFieldInit(props, () => {
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
            <$Cascader
                {...props}
                options={options}
                formInstance={formContext}
            />

        </Form.Item>
    )
}

export default FormCascader;

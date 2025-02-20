import React, {useEffect} from "react";
import {FormItemProps} from "@/components/form/types";
import {Cascader, Form} from "antd-mobile";
import {RightOutline} from "antd-mobile-icons";
import formFieldInit from "@/components/form/common";
import "./form.scss";

const valueToForm = (value:string)=>{
    if(value && value.length>0){
        return value.split(",");
    }
    return value;
}

const formToValue = (value:string[])=>{
    if(value && value.length>0){
        return value.join(",")
    }
    return value;
}

const FormCascader: React.FC<FormItemProps> = (props) => {


    const [visible, setVisible] = React.useState(false);
    const [options, setOptions] = React.useState(props.options);

    const {formAction,rules,validateContext} = formFieldInit(props,()=>{
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
            rules={rules}
            hidden={props.hidden}
            help={props.help}
            disabled={props.disabled}
            extra={(
                <RightOutline
                    onClick={() => {
                        setVisible(true);
                    }}
                />
            )}
            getValueProps={(value)=>{
                if(value) {
                    return {
                        value: valueToForm(value)
                    }
                }
                return value
            }}
        >
            <Cascader
                value={props.value}
                options={options || []}
                visible={visible}
                onClose={() => {
                    setVisible(false)
                }}
                onConfirm={(value) => {
                    formAction?.setFieldValue(props.name as string, formToValue(value as string[]));
                    if(formAction) {
                        validateContext?.validateField(props.name, formAction);
                    }
                    props.onChange && props.onChange(value, formAction);
                    setVisible(false);
                }}
            >
                {items => {
                    if (items.every(item => item === null)) {
                        return (
                            <span
                                onClick={() => {
                                    setVisible(true)
                                }}
                                className={"placeholder-span"}
                            >{props.placeholder || '请选择数据'}</span>
                        )
                    } else {
                        const value = items.map(item => item?.label ?? '请选择数据').join('-')
                        return (
                            <span
                                onClick={() => {
                                    setVisible(true)
                                }}
                            >{value}</span>
                        )
                    }
                }}
            </Cascader>
        </Form.Item>
    )
}

export default FormCascader;

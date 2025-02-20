import React from "react";
import {FormItemProps} from "@/components/form/types";
import {Form, Input} from "antd-mobile";
import {EyeInvisibleOutline, EyeOutline} from "antd-mobile-icons";
import formFieldInit from "@/components/form/common";

const FormPassword: React.FC<FormItemProps> = (props) => {

    const [visible, setVisible] = React.useState(false);

    const {formAction,rules,validateContext} = formFieldInit(props);

    return (
        <Form.Item
            name={props.name}
            label={props.label}
            rules={rules}
            hidden={props.hidden}
            help={props.help}
            disabled={props.disabled}
        >
            <div className={"form-password"}>
                <Input
                    type={visible?"text":"password"}
                    value={props.value}
                    placeholder={props.placeholder}
                    onChange={(e) => {
                        if(formAction) {
                            validateContext?.validateField(props.name, formAction);
                        }
                        props.onChange && props.onChange(e,formAction);
                    }}
                />
                <div className={"form-password-eye"}>
                    {!visible ? (
                        <EyeInvisibleOutline onClick={() => setVisible(true)}/>
                    ) : (
                        <EyeOutline onClick={() => setVisible(false)}/>
                    )}
                </div>
            </div>
        </Form.Item>
    )
}

export default FormPassword;

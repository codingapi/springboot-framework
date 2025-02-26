import React, {useEffect, useState} from "react";
import {FormItemProps} from "@/components/form/types";
import {Form, Input} from "antd";
import formFieldInit from "@/components/form/common";


const FormCaptcha: React.FC<FormItemProps> = (props) => {
    const [captchaImg, setCaptchaImg] = useState<string>('');
    const {formAction} = formFieldInit(props);

    const reloadCaptcha = () => {
        props.onCaptchaRefresh && props.onCaptchaRefresh().then((res) => {
            setCaptchaImg(res.url);
            props.onCaptchaChange && props.onCaptchaChange(res.code);
        });
    }

    useEffect(() => {
        reloadCaptcha();
    }, [])

    return (
        <Form.Item
            name={props.name}
            label={props.label}
            hidden={props.hidden}
            help={props.help}
            required={props.required}
        >
           <div className={"form-captcha"}>
               <Input
                   className={"form-captcha-input"}
                   disabled={props.disabled}
                   value={props.value}
                   placeholder={props.placeholder}
                   onChange={(value) => {
                       const currentValue = value.target.value;
                       formAction?.setFieldValue(props.name, currentValue);
                       props.onChange && props.onChange(currentValue,formAction);
                   }}
               />

               <img
                   className={"form-captcha-img"}
                   onClick={() => {
                       reloadCaptcha();
                   }}
                   src={captchaImg}
                   alt="点击重置"
               />
           </div>
        </Form.Item>
    )
}

export default FormCaptcha;

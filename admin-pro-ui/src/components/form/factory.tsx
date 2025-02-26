import React from "react";
import {FormField} from "@/components/form/types";
import FormInput from "@/components/form/input";
import FormPassword from "@/components/form/password";
import FormCaptcha from "@/components/form/captcha";
import FormCheckbox from "@/components/form/checkbox";
import FormRadio from "@/components/form/radio";
import FormRate from "@/components/form/rate";
import FormSlider from "@/components/form/slider";
import FormStepper from "@/components/form/stepper";
import FormTextArea from "@/components/form/textarea";
import FormSwitch from "@/components/form/switch";
import FormDate from "@/components/form/date";
import FormCascader from "@/components/form/cascader";
import FormSelect from "@/components/form/select";


class FormFactory {

    static create = (field: FormField) => {
        const type = field.type;
        const props = field.props;

        if (type === 'input') {
            return (
                <FormInput
                    {...props}
                    key={props.name}
                />
            )
        }

        if (type === 'password') {
            return (
                <FormPassword
                    {...props}
                    key={props.name}
                />
            )
        }

        if (type === 'captcha') {
            return (
                <FormCaptcha
                    {...props}
                    key={props.name}
                />
            )
        }

        if (type === 'checkbox') {
            return (
                <FormCheckbox
                    {...props}
                    key={props.name}
                />
            )
        }

        if (type === 'radio') {
            return (
                <FormRadio
                    {...props}
                    key={props.name}
                />
            )
        }

        if (type === 'rate') {
            return (
                <FormRate
                    {...props}
                    key={props.name}
                />
            )
        }

        if (type === 'slider') {
            return (
                <FormSlider
                    {...props}
                    key={props.name}
                />
            )
        }
        if (type === 'stepper') {
            return (
                <FormStepper
                    {...props}
                    key={props.name}
                />
            )
        }

        if (type === 'textarea') {
            return (
                <FormTextArea
                    {...props}
                    key={props.name}
                />
            )
        }

        if (type === 'switch') {
            return (
                <FormSwitch
                    {...props}
                    key={props.name}
                />
            )
        }

        if (type === 'date') {
            return (
                <FormDate
                    {...props}
                    key={props.name}
                />
            )
        }

        if (type === 'cascader') {
            return (
                <FormCascader
                    {...props}
                    key={props.name}
                />
            )
        }

        if (type === 'selector') {
            return (
                <FormCheckbox
                    {...props}
                    key={props.name}
                />
            )
        }

        if (type === 'select') {
            return (
                <FormSelect
                    {...props}
                    key={props.name}
                />
            )
        }

        return (
            <></>
        )
    }

}

export default FormFactory;

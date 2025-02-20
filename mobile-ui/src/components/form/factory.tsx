import React from "react";
import {FormField} from "@/components/form/types";
import FormInput from "@/components/form/input";
import FormPassword from "@/components/form/password";
import FormSelect from "@/components/form/select";
import FormDate from "@/components/form/date";
import FormRadio from "@/components/form/radio";
import FormTextArea from "@/components/form/textarea";
import FormCascader from "@/components/form/cascader";
import FormCheckbox from "@/components/form/checkbox";
import FormUploader from "@/components/form/uploder";
import FormSwitch from "@/components/form/switch";
import FormStepper from "@/components/form/stepper";
import FormSlider from "@/components/form/slider";
import FormRate from "@/components/form/rate";
import FormSelector from "@/components/form/selector";
import FormCaptcha from "@/components/form/captcha";

class FormFactory {

    static create = (field: FormField) => {
        const type = field.type;
        const props = field.props;

        if (type === 'input') {
            return (
                <FormInput
                    {...props}
                />
            )
        }

        if (type === 'captcha') {
            return (
                <FormCaptcha
                    {...props}
                />
            )
        }

        if (type === 'password') {
            return (
                <FormPassword
                    {...props}
                />
            )
        }

        if (type === 'cascader') {
            return (
                <FormCascader
                    {...props}
                />
            )
        }

        if (type === 'select') {
            return (
                <FormSelect
                    {...props}
                />
            )
        }

        if (type === 'date') {
            return (
                <FormDate
                    {...props}
                />
            )
        }

        if (type === 'radio') {
            return (
                <FormRadio
                    {...props}
                />
            )
        }

        if (type === 'checkbox') {
            return (
                <FormCheckbox
                    {...props}
                />
            )
        }

        if (type === 'textarea') {
            return (
                <FormTextArea
                    {...props}
                />
            )
        }

        if (type === 'uploader') {
            return (
                <FormUploader
                    {...props}
                />
            )
        }

        if (type === 'switch') {
            return (
                <FormSwitch
                    {...props}
                />
            )
        }

        if (type === 'stepper') {
            return (
                <FormStepper
                    {...props}
                />
            )
        }

        if (type === 'slider') {
            return (
                <FormSlider
                    {...props}
                />
            )
        }

        if (type === 'rate') {
            return (
                <FormRate
                    {...props}
                />
            )
        }

        if (type === 'selector') {
            return (
                <FormSelector
                    {...props}
                />
            )
        }
    }

}

export default FormFactory;

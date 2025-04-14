import {FormValidateContext} from "@/components/form/validate";
import {FormFieldOptionListenerContext, FormFieldReloadListenerContext} from "@/components/form/listener";
import {FormInstance as MobileFormInstance} from "rc-field-form/es/interface";
import {FormField} from "@/components/form/types";
import {NamePath} from "antd-mobile/es/components/form";
import {Form as MobileForm, Toast} from "antd-mobile";
import {FiledData, FormAction} from "@/components/form";
import React from "react";

class FormInstance {
    private readonly validateContext: FormValidateContext;
    private readonly reloadContext: FormFieldReloadListenerContext;
    private readonly optionContext: FormFieldOptionListenerContext;
    private readonly formInstance: MobileFormInstance;
    private readonly formAction: FormAction;
    private fields: FormField[];

    private fieldsUpdateDispatch: React.Dispatch<React.SetStateAction<FormField[]>> | undefined;

    public setFieldsUpdateDispatch = (fieldsUpdateDispatch: React.Dispatch<React.SetStateAction<FormField[]>>) => {
        this.fieldsUpdateDispatch = fieldsUpdateDispatch;
    }

    private updateFields = (resetFields:(prevState: FormField[]) => FormField[]) => {
        this.fields = resetFields(this.fields);
        if (this.fieldsUpdateDispatch) {
            this.fieldsUpdateDispatch(resetFields);
        }
    }

    private namePathEqual = (name1: NamePath, name2: NamePath) => {
        if (Array.isArray(name1) && Array.isArray(name2)) {
            if (name1.length !== name2.length) {
                return false;
            }
            for (let i = 0; i < name1.length; i++) {
                if (name1[i] !== name2[i]) {
                    return false;
                }
            }
            return true;
        }
        return name1 === name2;
    }

    public submit = async () => {
        const res = await this.validateContext.validate(this);
        if (res) {
            this.formInstance.submit();
        }
    }

    public reset = (values?: any) => {
        this.formInstance.resetFields();
        if (values) {
            this.formInstance.setFieldsValue(values);
            this.reloadContext.notifyAll();
        }
    }

    public hidden = (name: NamePath) => {
        if (this.fields.length == 0) {
            Toast.show('表单项未加载');
            return;
        }
        this.updateFields(prevFields => prevFields.map((field) => {
            if (this.namePathEqual(field.props.name, name)) {
                return {
                    ...field,
                    props: {
                        ...field.props,
                        hidden: true,
                        required: false
                    }
                }
            }
            return field;
        }));
        this.validateContext.clear();
    }

    public required = (name: NamePath, required: boolean) => {
        if (this.fields.length == 0) {
            Toast.show('表单项未加载');
            return;
        }
        this.updateFields(prevFields => prevFields.map((field) => {
            if (this.namePathEqual(field.props.name,name)) {
                return {
                    ...field,
                    props: {
                        ...field.props,
                        required: required
                    }
                }
            }
            return field;
        }));
        this.validateContext.clear();
    }

    public show = (name: NamePath) => {
        if (this.fields.length == 0) {
            Toast.show('表单项未加载');
            return;
        }
        this.updateFields(prevFields => prevFields.map((field) => {
            if (this.namePathEqual(field.props.name,name)) {
                return {
                    ...field,
                    props: {
                        ...field.props,
                        hidden: false
                    }
                }
            }
            return field;
        }));
        this.validateContext.clear();
    }

    public disable = (name: NamePath) => {
        if (this.fields.length == 0) {
            Toast.show('表单项未加载');
            return;
        }
        this.updateFields(prevFields => prevFields.map((field) => {
            if (this.namePathEqual(field.props.name,name)) {
                return {
                    ...field,
                    props: {
                        ...field.props,
                        disabled: true
                    }
                }
            }
            return field;
        }));
        this.validateContext.clear();
    }

    public disableAll = () => {
        if (this.fields.length == 0) {
            Toast.show('表单项未加载');
            return;
        }
        this.updateFields(prevFields => prevFields.map((field) => {
            return {
                ...field,
                props: {
                    ...field.props,
                    disabled: true
                }
            }
        }));
        this.validateContext.clear();
    }

    public enable = (name: NamePath) => {
        if (this.fields.length == 0) {
            Toast.show('表单项未加载');
            return;
        }
        this.updateFields(prevFields => prevFields.map((field) => {
            if (this.namePathEqual(field.props.name,name)) {
                return {
                    ...field,
                    props: {
                        ...field.props,
                        disabled: false
                    }
                }
            }
            return field;
        }));
        this.validateContext.clear();
    }

    public enableAll = () => {
        if (this.fields.length == 0) {
            Toast.show('表单项未加载');
            return;
        }
        this.updateFields(prevFields => prevFields.map((field) => {
            return {
                ...field,
                props: {
                    ...field.props,
                    disabled: false
                }
            }
        }));
        this.validateContext.clear();
    }

    public remove = (name: NamePath) => {
        if (this.fields.length == 0) {
            Toast.show('表单项未加载');
            return;
        }
        this.updateFields(prevFields => prevFields.filter((field) => !this.namePathEqual(field.props.name,name)));
        this.validateContext.clear();
    }

    public create = (field: FormField, index?: number) => {
        if (this.fields.length == 0) {
            Toast.show('表单项未加载');
            return;
        }
        this.updateFields(prevFields => {
            const filteredFields = prevFields.filter((item) => item.props.name !== field.props.name);
            if (index === undefined || index < 0) {
                return [...filteredFields, field];
            } else {
                const newFields = [...filteredFields];
                newFields.splice(index, 0, field);
                return newFields;
            }
        });
        this.validateContext.clear();
    }

    public getFieldValue = (name: NamePath) => {
        return this.formInstance.getFieldValue(name);
    }

    public getFieldsValue = () => {
        return this.formInstance.getFieldsValue();
    }

    public getFieldProps = (name: NamePath) => {
        for (const field of this.fields) {
            if (this.namePathEqual(field.props.name, name)) {
                return field;
            }
        }
        return null;
    }

    public reloadOptions = (name: NamePath) => {
        this.optionContext.notify(name);
    }

    public reloadAllOptions = () => {
        this.optionContext.notifyAll();
    }

    public setFieldValue = (name: NamePath, value: any) => {
        this.formInstance.setFieldValue(name, value);
        this.reloadContext.notify(name);
        this.validateContext?.validateField(name, this);
    }

    public setFieldsValue = (values: any) => {
        this.formInstance.setFieldsValue(values);
        this.reloadContext.notifyAll();
    }

    public setFields = (fields: FiledData[]) => {
        this.formInstance.setFields(fields);
    }

    public validate = () => {
        return this.validateContext.validate(this);
    }

    public resetFields = (fields:FormField[]) => {
        this.fields = fields;
    }

    constructor() {
        this.validateContext = new FormValidateContext();
        this.reloadContext = new FormFieldReloadListenerContext();
        this.optionContext = new FormFieldOptionListenerContext();
        this.formInstance = MobileForm.useForm()[0];
        this.fields = [];
        this.formAction = {
            submit: this.submit,
            reset: this.reset,
            hidden: this.hidden,
            show: this.show,
            remove: this.remove,
            create: this.create,
            disable: this.disable,
            disableAll: this.disableAll,
            enable: this.enable,
            enableAll: this.enableAll,
            required: this.required,
            getFieldValue: this.getFieldValue,
            getFieldsValue: this.getFieldsValue,
            getFieldProps: this.getFieldProps,
            reloadOptions: this.reloadOptions,
            reloadAllOptions: this.reloadAllOptions,
            setFieldValue: this.setFieldValue,
            setFieldsValue: this.setFieldsValue,
            setFields: this.setFields,
            validate: this.validate,
        }
    }

    public getFormAction = () => {
        return this.formAction;
    }

    public getFormValidateContext = () => {
        return this.validateContext;
    }

    public getFormFieldReloadListenerContext = () => {
        return this.reloadContext;
    }

    public getFormFieldOptionListenerContext = () => {
        return this.optionContext;
    }

    public getFormControlInstance = ():MobileFormInstance => {
        return this.formInstance;
    }

}

export default FormInstance;

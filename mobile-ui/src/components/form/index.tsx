import React, {useEffect} from "react";
import {Form as MobileForm} from "antd-mobile";
import FormFactory from "@/components/form/factory";
import {FormField} from "@/components/form/types";
import {FormValidateContext} from "@/components/form/validate";
import {NamePath} from "antd-mobile/es/components/form";
import {FormFieldOptionListenerContext, FormFieldReloadListenerContext} from "@/components/form/listener";
import "./form.scss";

export interface FiledData {
    name: NamePath;
    errors?: string[];
}

export interface FormAction {
    // 提交表单，提取之前会先校验表单
    submit: () => void;
    // 重置表单，恢复表单项最初的状态
    reset: (values?: any) => void;
    // 隐藏表单项，隐藏后的表单项不会显示在表单中，但是值会被提交
    hidden: (name: NamePath) => void;
    // 展示表单项，展示后的表单项会显示在表单中，值也会被提交
    show: (name: NamePath) => void;
    // 删除表单项，删除后的表单项不会显示在表单中，值也不会被提交，在fields配置的情况下生效
    remove: (name: NamePath) => void;
    // 添加表单项，添加后的表单项会显示在表单中，值也会被提交，在fields配置的情况下生效
    create: (field: FormField, index?: number) => void;
    // 禁用表单项，禁用后的表单项还会被提交
    disable: (name: NamePath) => void;
    // 全部禁用，禁用后的表单项还会被提交
    disableAll:()=>void;
    // 启用表单项，启用后的表单项还会被提交
    enable: (name: NamePath) => void;
    // 全部启用，启用后的表单项还会被提交
    enableAll:()=>void;
    // 必填选项控制,true为必填false为非必填提示
    required: (name: NamePath,required:boolean) => void;
    // 获取字段的值
    getFieldValue: (name: NamePath) => any;
    // 重新加载选项
    reloadOptions: (name: NamePath) => any;
    // 重新加载所有选项
    reloadAllOptions: () => any;
    // 获取全部字段的值
    getFieldsValue: () => any;
    // 设置字段的值
    setFieldValue: (name: NamePath, value: any) => void;
    // 设置全部字段的值
    setFieldsValue: (values: any) => void;
    // 设置Field字段
    setFields: (fields: FiledData[]) => void;
    // 校验表单
    validate: () => Promise<boolean>;
}

export interface FormProps {
    // 表单字段
    loadFields?: ()=>Promise<FormField[]>;
    // 表单提交事件
    onFinish?: (values: any) => Promise<void>;
    // 表单控制对象
    actionRef?: React.Ref<FormAction>;
    // form布局，默认vertical
    layout?: 'horizontal' | 'vertical';
    // children元素
    children?: React.ReactNode;
    // footer元素
    footer?: React.ReactNode;
    // 初始化值
    initialValues?: any;
}

interface FormContextProps {
    // form表单的控制对象
    formAction: FormAction;
    // 检验控制对象
    validateContext: FormValidateContext;
    // 表单刷新监听对象
    reloadContext:FormFieldReloadListenerContext;
    // 选项刷新监听对象
    optionContext:FormFieldOptionListenerContext;
}

export const FormContext = React.createContext<FormContextProps | null>(null);


const namePathEqual = (name1: NamePath, name2: NamePath): boolean => {
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


const Form: React.FC<FormProps> = (props) => {

    const [form] = MobileForm.useForm();

    const validateContext = new FormValidateContext();
    const reloadContext = new FormFieldReloadListenerContext();
    const optionContext = new FormFieldOptionListenerContext();

    const formAction = {
        submit: async () => {
            const res = await validateContext.validate(formAction);
            if (res) {
                form.submit();
            }
        },

        reset: (values?: any) => {
            reloadFields();
            form.resetFields();
            if (values) {
                form.setFieldsValue(values);
                reloadContext.notifyAll();
            }
        },

        hidden: (name: NamePath) => {
            setFields(prevFields => prevFields.map((field) => {
                if (namePathEqual(field.props.name,name)) {
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
            validateContext.clear();
        },

        required:(name: NamePath,required:boolean) => {
            setFields(prevFields => prevFields.map((field) => {
                if (namePathEqual(field.props.name,name)) {
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
            validateContext.clear();
        },

        show: (name: NamePath) => {
            setFields(prevFields => prevFields.map((field) => {
                if (namePathEqual(field.props.name,name)) {
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
            validateContext.clear();
        },

        disable: (name: NamePath) => {
            setFields(prevFields => prevFields.map((field) => {
                if (namePathEqual(field.props.name,name)) {
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
            validateContext.clear();
        },

        disableAll:()=>{
            setFields(prevFields => prevFields.map((field) => {
                return {
                    ...field,
                    props: {
                        ...field.props,
                        disabled: true
                    }
                }
            }));
            validateContext.clear();
        },

        enable: (name: NamePath) => {
            setFields(prevFields => prevFields.map((field) => {
                if (namePathEqual(field.props.name,name)) {
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
            validateContext.clear();
        },

        enableAll:()=>{
            setFields(prevFields => prevFields.map((field) => {
                return {
                    ...field,
                    props: {
                        ...field.props,
                        disabled: false
                    }
                }
            }));
            validateContext.clear();
        },

        remove: (name: NamePath) => {
            setFields(prevFields => prevFields.filter((field) => !namePathEqual(field.props.name,name)));
            validateContext.clear();
        },

        create: (field: FormField, index?: number) => {
            setFields(prevFields => {
                const filteredFields = prevFields.filter((item) => item.props.name !== field.props.name);
                if (index === undefined || index < 0) {
                    return [...filteredFields, field];
                } else {
                    const newFields = [...filteredFields];
                    newFields.splice(index, 0, field);
                    return newFields;
                }
            });
            validateContext.clear();
        },

        getFieldValue(name: NamePath): any {
            return form.getFieldValue(name);
        },

        getFieldsValue(): any {
            return form.getFieldsValue();
        },

        reloadOptions:(name: NamePath) => {
            optionContext.notify(name);
        },

        reloadAllOptions:()=>{
            optionContext.notifyAll();
        },

        setFieldValue(name: NamePath, value: any): void {
            form.setFieldValue(name, value);
            reloadContext.notify(name);
            validateContext?.validateField(name, formAction);
        },

        setFieldsValue(values: any): void {
            form.setFieldsValue(values);
            reloadContext.notifyAll();
        },

        setFields(fields: FiledData[]): void {
            form.setFields(fields);
        },

        validate(): Promise<boolean> {
            return validateContext.validate(formAction);
        }
    }

    const formContextProps = {
        formAction: formAction,
        validateContext: validateContext,
        reloadContext:reloadContext,
        optionContext:optionContext
    }

    const [fields, setFields] = React.useState<FormField[]>([]);

    const reloadFields = ()=>{
        if(props.loadFields){
            props.loadFields().then(fields=>{
                setFields(fields);
            })
        }
    }

    useEffect(() => {
        reloadFields();
    }, []);


    React.useImperativeHandle(props.actionRef, () => {
        return formAction
    }, [fields]);

    return (
        <FormContext.Provider
            value={formContextProps}
        >
            <MobileForm
                form={form}
                onFinish={(values) => {
                    props.onFinish && props.onFinish(values);
                }}
                initialValues={props.initialValues}
                layout={props.layout}
                footer={props.footer}
            >
                {fields.length > 0 && fields.map((field) => {
                    return FormFactory.create(field) as React.ReactNode;
                })}

                {props.children}

            </MobileForm>
        </FormContext.Provider>
    )
}

export default Form;

import React, {useEffect} from "react";
import {Form as MobileForm} from "antd-mobile";
import FormFactory from "@/components/form/factory";
import {FormField} from "@/components/form/types";
import {NamePath} from "antd-mobile/es/components/form";
import "./form.scss";
import FormInstance from "@/components/form/domain/FormInstance";

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
    // 获取Field属性
    getFieldProps: (name: NamePath) => FormField | null;
    // 校验表单
    validate: () => Promise<boolean>;
}

export interface FormProps {
    // 表单字段
    loadFields?: ()=>Promise<FormField[]>;
    // 表单提交事件
    onFinish?: (values: any) => Promise<void>;
    // form布局，默认vertical
    layout?: 'horizontal' | 'vertical';
    // children元素
    children?: React.ReactNode;
    // footer元素
    footer?: React.ReactNode;
    // 初始化值
    initialValues?: any;
    // 表单实例
    form?: FormInstance;
}


export const FormContext = React.createContext<FormInstance | null>(null);

const FormComponent: React.FC<FormProps> = (props) => {

    const formInstance = props.form? props.form : new FormInstance();

    const [fields, setFields] = React.useState<FormField[]>([]);
    formInstance.setFieldsUpdateDispatch(setFields);

    const formControl = formInstance.getFormControlInstance() ;

    const reloadFields = ()=>{
        if(props.loadFields){
            props.loadFields().then(fields=>{
                setFields(fields);
                formInstance.resetFields(fields);
            })
        }
    }

    useEffect(() => {
        reloadFields();
    }, [props.loadFields]);


    return (
        <FormContext.Provider
            value={formInstance}
        >
            <MobileForm
                form={formControl}
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

type FormType = typeof FormComponent;
type FormComponentType = FormType & {
    useForm: ()=>FormInstance;
};

const Form = FormComponent as FormComponentType;
Form.useForm = ()=>{
    return new FormInstance();
};

export default Form;


import {NamePath} from "antd-mobile/es/components/form";
import {FormValidateContent} from "@/components/form/validate";
import {FormAction} from "@/components/form";
import React from "react";

// Form表单选项类型
export interface FormOption {
    label: string;
    value: string;
    disable?: boolean;
    children?: FormOption[];
}

// Form表单类型
type FormFieldType =
    "input" | "cascader" | "select" | "password" | "date" |
    "radio" | "textarea" | "checkbox" | "uploader" | "switch" |
    "stepper" | "slider" | "rate" | "selector" | "captcha";

// FormField
export interface FormField {
    // 表单字段类型
    type: FormFieldType;
    // 表单字段属性
    props: FormItemProps;
}

// 自定义Select组件选项维护视图
export interface SelectOptionFormEditProps {
    // 当前表单操作对象
    currentAction?: React.RefObject<FormAction>;
    // 父级表单操作对象
    formAction?: FormAction;
}

// Form表单字段属性
export interface FormItemProps {
    // 是否隐藏字段
    hidden?: boolean;
    // 是否禁用
    disabled?: boolean;
    // 是否必填,当为true时会自动给rules添加required校验
    required?: boolean;
    // 表单字段名
    name?: NamePath;
    // 表单字段标签
    label?: string;
    // 帮助提示信息
    help?: string;
    // 表单值
    value?: any;
    // 输入提示
    placeholder?: string;
    // 变更事件
    onChange?: (value: any, form?: FormAction) => void;
    // 静态选项，对应loadOptions的动态选项，仅限于select、radio等组件有效
    options?: FormOption[],
    // 动态加载选项,仅限于select、radio等组件有效
    loadOptions?: (form?: FormAction) => Promise<FormOption[]>,
    // 动态校验函数,尽在fields模式下生效
    validateFunction?: (content: FormValidateContent) => Promise<string[]>,

    /** 以下为表单字段的拓展熟悉，非公共属性 **/
    // 单选框方向
    radioDirection?: "vertical" | "horizontal",
    // 多选框方向
    checkboxDirection?: "vertical" | "horizontal",
    // TextArea输入行数
    textAreaRows?: number,
    // TextArea输入框最大值
    textAreaMaxLength?: number,
    // select组件是否支持多选
    selectMultiple?: boolean,
    // select组件添加的视图是否开启编辑
    selectOptionFormEditable?: boolean,
    // select组件添加的视图
    selectOptionFormEditView?: React.ComponentType<SelectOptionFormEditProps>,
    // select组件添加的视图title，默认添加选项
    selectOptionFormEditTitle?: string,
    // select组件添加的视图footer确定按钮文字，默认添加选项
    selectOptionFormEditFooterOkText?: string,
    // select组件添加的视图footer取消按钮文字，默认取消添加
    selectOptionFormEditFooterCancelText?: string,
    // Select组件添加数据事件
    onSelectOptionFormFinish?: (formAction: FormAction,
                                selectOptionFormEditFormAction: FormAction,
                                reloadOption?: () => void,
                                close?: () => void) => void;

    // 文件上传接受的文件类型，默认为 image/*
    uploaderAccept?: string,
    // 文件上传最大数量
    uploaderMaxCount?: number,
    // input输入框最大值
    inputMaxLength?: number,
    // input输入框类型，默认为text
    inputType?: "text" | "number",
    // date组件的日期格式，默认YYYY-MM-DD
    dateFormat?: string,
    // date组件的精度，默认为day
    datePrecision?: "year" | "month" | "day" | "hour" | "minute" | "second" | "week" | "week-day" | "quarter",
    // switch选择文本
    switchCheckText?: string,
    // switch未选择文本
    switchUnCheckText?: string,
    // stepper组件的最大值
    stepperMaxNumber?: number,
    // stepper组件的最小值
    stepperMinNumber?: number,
    // stepper组件的小数位
    stepperDecimalLength?: number,
    // slider组件的最大值
    sliderMaxNumber?: number,
    // slider组件的最小值
    sliderMinNumber?: number,
    // slider组件的拖动步距
    sliderStep?: number,
    // slider组件实现展示刻度
    sliderTicks?: boolean,
    // slider组件悬浮提示
    sliderPopover?: boolean,
    // slider组件是否双向滑动
    sliderRange?: boolean,
    // slider组件的刻度尺
    sliderMarks?: any,
    // rate的star总数
    rateCount?: number,
    // rate允许半选
    rateAllowHalf?: boolean,
    // selector组件是否多选
    selectorMultiple?: boolean,
    // selector组件每行展示数量
    selectorColumn?: number,
    // Captcha组件切换验证码事件
    onCaptchaChange?: (value: string) => void;
    // Captcha组件刷新验证码事件
    onCaptchaRefresh?: () => Promise<{
        // 验证码标志
        code: string;
        // 验证码图片地址
        url: string;
    }>;
    // 文件上传事件
    onUploaderUpload?: (filename: string, base64: string) => Promise<{
        // 文件id
        id: string,
        // 文件名
        name: string
        // 文件地址
        url: string;
    }>
    // 文件加载事件
    onUploaderLoad?: (ids: string) => Promise<{
        // 文件id
        id: string,
        // 文件名
        name: string
        // 文件地址
        url: string;
    }[]>

}



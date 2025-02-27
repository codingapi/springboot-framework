import React from 'react';
import {PageContainer} from "@ant-design/pro-components";
import './index.scss';
import Form, {FormAction} from "@/components/form";
import FormInput from "@/components/form/input";
import {Button, Col, message, Row} from "antd";
import {FormField} from "@/components/form/types";
import FormPassword from "@/components/form/password";
import FormCaptcha from "@/components/form/captcha";
import FormCheckbox from "@/components/form/checkbox";
import FormRadio from "@/components/form/radio";
import FormRate from "@/components/form/rate";
import FormSlider from "@/components/form/slider";
import FormStepper from "@/components/form/stepper";
import FormSwitch from "@/components/form/switch";
import FormTextArea from "@/components/form/textarea";
import FormDate from "@/components/form/date";
import FormCascader from "@/components/form/cascader";
import FormSelect from "@/components/form/select";
import FormUploader from "@/components/form/uploder";


const FooterButtons: React.FC<{ formAction: React.RefObject<FormAction> }> = ({formAction}) => {
    const data = {
        user: {
            name: '张三',
            age: 18,
            password: '123456',
            code: '123',
            checkbox: '1,2',
            radio: '1',
            rate: 3,
            slider: 50,
            switch: true,
            textarea: '这是一段文本',
            date: '2021-08-01',
            cascader: '1,1-1,1-1-1',
            select: '1,2',
            avatar: 'c84fb304c180f61bb7db40efef7f85b7'
        }
    }

    return (
        <div
            style={{
                display: "grid",
                gridTemplateColumns: "1fr 1fr",
                gridRowGap: "10px",
                gridColumnGap: "10px"
            }}
        >
            <Button
                onClick={async () => {
                    const name = formAction.current?.getFieldValue(["user", "name"])
                    message.success(name);
                }}
            >获取姓名</Button>

            <Button
                onClick={async () => {
                    formAction.current?.setFieldValue(["user", "name"], "123")
                }}
            >设置姓名</Button>

            <Button
                onClick={async () => {
                    formAction.current?.validate();
                }}
            >验证表单</Button>

            <Button
                onClick={async () => {
                    formAction.current?.submit();
                }}
            >提交表单</Button>

            <Button
                onClick={async () => {
                    const values = formAction.current?.getFieldsValue();
                    message.success(JSON.stringify(values));
                }}
            >获取表单值</Button>

            <Button
                onClick={async () => {
                    formAction.current?.reset();
                }}
            >重置表单</Button>

            <Button
                onClick={async () => {
                    formAction.current?.setFieldsValue(data);
                }}
            >表单赋值</Button>
            <div></div>

            <Button
                onClick={async () => {
                    formAction.current?.enable(["user", "name"]);
                }}
            >启用姓名字段</Button>

            <Button
                onClick={async () => {
                    formAction.current?.disable(["user", "name"]);
                }}
            >禁用姓名字段</Button>


            <Button
                onClick={async () => {
                    formAction.current?.hidden(["user", "name"]);
                }}
            >隐藏姓名字段</Button>

            <Button
                onClick={async () => {
                    formAction.current?.show(["user", "name"]);
                }}
            >展示姓名字段</Button>

            <Button
                onClick={async () => {
                    formAction.current?.remove(["user", "name"]);
                }}
            >删除姓名字段</Button>

            <Button
                onClick={async () => {
                    formAction.current?.create({
                       props:{
                           required: true,
                           name: ['user', 'name'],
                           label: '姓名',
                           placeholder: '请输入姓名',
                           validateFunction: async (content) => {
                               const value = content.value;
                               if (value) {
                                   return []
                               }
                               return ['姓名不能为空']
                           }
                       },
                       type:'input'
                    },0);
                }}
            >添加姓名字段</Button>

        </div>
    )
}

const WelcomePage = () => {

    const leftFormAction = React.useRef<FormAction>(null);
    const rightFormAction = React.useRef<FormAction>(null);

    const fields = [
        {
            type: 'input',
            props: {
                required: true,
                name: ['user', 'name'],
                label: '姓名',
                placeholder: '请输入姓名',
                validateFunction: async (content) => {
                    const value = content.value;
                    if (value) {
                        return []
                    }
                    return ['姓名不能为空']
                }
            }
        },
        {
            type: 'stepper',
            props: {
                required: true,
                name: ['user', 'age'],
                label: '年龄',
                placeholder: '请输入年龄',
            }
        },
        {
            type: 'password',
            props: {
                required: true,
                name: ['user', 'password'],
                label: '银行卡密码',
                placeholder: '请输入银行卡密码',
                validateFunction: async (content) => {
                    const value = content.value;
                    if (value) {
                        return []
                    }
                    return ['银行卡密码不能为空']
                }
            }
        },
        {
            type: 'captcha',
            props: {
                required: true,
                name: ['user', 'code'],
                label: '银行卡验证码',
                placeholder: '请输入银行卡验证码',
                onCaptchaRefresh: async () => {
                    console.log('refresh captcha')
                    return {
                        url: '/captcha.jpeg',
                        code: '123'
                    }
                }
            }
        },
        {
            type: 'checkbox',
            props: {
                required: true,
                name: ['user', 'checkbox'],
                label: '复选框',
                options: [
                    {label: '选项1', value: '1'},
                    {label: '选项2', value: '2'},
                    {label: '选项3', value: '3'},
                ]
            }
        },
        {
            type: 'radio',
            props: {
                required: true,
                name: ['user', 'radio'],
                label: '单选框',
                options: [
                    {label: '选项1', value: '1'},
                    {label: '选项2', value: '2'},
                    {label: '选项3', value: '3'},
                ]
            }
        },
        {
            type: 'rate',
            props: {
                required: true,
                name: ['user', 'rate'],
                label: '评分',
            }
        },
        {
            type: 'slider',
            props: {
                required: true,
                name: ['user', 'slider'],
                label: '滑块',
                sliderPopover: true
            }
        },
        {
            type: 'switch',
            props: {
                required: true,
                name: ['user', 'switch'],
                label: '开关',
            }
        },
        {
            type: 'textarea',
            props: {
                required: true,
                name: ['user', 'textarea'],
                label: '文本域',
            }
        },
        {
            type: 'date',
            props: {
                required: true,
                name: ['user', 'date'],
                label: '日期',
            }
        },
        {
            type: 'cascader',
            props: {
                required: true,
                name: ['user', 'cascader'],
                label: '级联选择',
                options: [
                    {
                        label: '选项1',
                        value: '1',
                        children: [
                            {
                                label: '选项1-1',
                                value: '1-1',
                                children: [
                                    {
                                        label: '选项1-1-1',
                                        value: '1-1-1',
                                    },
                                    {
                                        label: '选项1-1-2',
                                        value: '1-1-2',
                                    },
                                ]
                            },
                            {
                                label: '选项1-2',
                                value: '1-2',
                            },
                        ]
                    },
                    {
                        label: '选项2',
                        value: '2',
                        children: [
                            {
                                label: '选项2-1',
                                value: '2-1',
                            },
                            {
                                label: '选项2-2',
                                value: '2-2',
                            },
                        ]
                    },
                ]
            }
        },
        {
            type: 'select',
            props: {
                required: true,
                name: ['user', 'select'],
                label: '选择器',
                selectMultiple: true,
                options: [
                    {label: '选项1', value: '1'},
                    {label: '选项2', value: '2'},
                    {label: '选项3', value: '3'},
                ]
            }
        },
        {
            type: 'uploader',
            props: {
                required: true,
                name: ['user', 'avatar'],
                label: '头像',
            }
        },
    ] as FormField[];


    return (
        <PageContainer>
            <Row gutter={[24, 24]}>
                <Col span={12}>
                    <Form
                        actionRef={leftFormAction}
                        layout={"horizontal"}
                        footer={(
                            <FooterButtons
                                formAction={leftFormAction}
                            />
                        )}
                    >
                        <FormInput
                            required={true}
                            name={["user", "name"]}
                            label={"姓名"}
                            placeholder={"请输入姓名"}
                            validateFunction={async (content) => {
                                const value = content.value;
                                if (value) {
                                    return []
                                }
                                return ['姓名不能为空']
                            }}
                        />

                        <FormStepper
                            required={true}
                            name={["user", "age"]}
                            label={"年龄"}
                            placeholder={"请输入年龄"}
                        />

                        <FormPassword
                            required={true}
                            name={["user", "password"]}
                            label={"银行卡密码"}
                            placeholder={"请输入银行卡密码"}
                            validateFunction={async (content) => {
                                const value = content.value;
                                if (value) {
                                    return []
                                }
                                return ['银行卡密码不能为空']
                            }}
                        />

                        <FormCaptcha
                            required={true}
                            name={["user", "code"]}
                            label={"银行卡验证码"}
                            placeholder={"请输入银行卡验证码"}
                            onCaptchaRefresh={async () => {
                                console.log('refresh captcha')
                                return {
                                    url: '/captcha.jpeg',
                                    code: '123'
                                }
                            }}
                        />

                        <FormCheckbox
                            required={true}
                            name={["user", "checkbox"]}
                            label={"复选框"}
                            options={[
                                {label: '选项1', value: '1'},
                                {label: '选项2', value: '2'},
                                {label: '选项3', value: '3'},
                            ]}
                        />

                        <FormRadio
                            required={true}
                            name={["user", "radio"]}
                            label={"单选框"}
                            options={[
                                {label: '选项1', value: '1'},
                                {label: '选项2', value: '2'},
                                {label: '选项3', value: '3'},
                            ]}
                        />

                        <FormRate
                            required={true}
                            name={["user", "rate"]}
                            label={"评分"}
                        />

                        <FormSlider
                            required={true}
                            name={["user", "slider"]}
                            label={"滑块"}
                            sliderPopover={true}
                        />

                        <FormSwitch
                            required={true}
                            name={["user", "switch"]}
                            label={"开关"}
                        />

                        <FormTextArea
                            required={true}
                            name={["user", "textarea"]}
                            label={"文本域"}
                        />

                        <FormDate
                            required={true}
                            name={["user", "date"]}
                            label={"日期"}
                        />

                        <FormCascader
                            required={true}
                            name={["user", "cascader"]}
                            label={"级联选择"}
                            options={[
                                {
                                    label: '选项1',
                                    value: '1',
                                    children: [
                                        {
                                            label: '选项1-1',
                                            value: '1-1',
                                            children: [
                                                {
                                                    label: '选项1-1-1',
                                                    value: '1-1-1',
                                                },
                                                {
                                                    label: '选项1-1-2',
                                                    value: '1-1-2',
                                                },
                                            ]
                                        },
                                        {
                                            label: '选项1-2',
                                            value: '1-2',
                                        },
                                    ]
                                },
                                {
                                    label: '选项2',
                                    value: '2',
                                    children: [
                                        {
                                            label: '选项2-1',
                                            value: '2-1',
                                        },
                                        {
                                            label: '选项2-2',
                                            value: '2-2',
                                        },
                                    ]
                                },
                            ]}
                        />

                        <FormSelect
                            required={true}
                            name={["user", "select"]}
                            label={"选择器"}
                            selectMultiple={true}
                            options={[
                                {label: '选项1', value: '1'},
                                {label: '选项2', value: '2'},
                                {label: '选项3', value: '3'},
                            ]}
                        />

                        <FormUploader
                            required={true}
                            name={["user", "avatar"]}
                            label={"头像"}
                        />
                    </Form>
                </Col>

                <Col span={12}>
                    <Form
                        actionRef={rightFormAction}
                        footer={(
                            <FooterButtons
                                formAction={rightFormAction}
                            />
                        )}
                        loadFields={async () => {
                            return fields;
                        }}
                    >
                    </Form>
                </Col>
            </Row>

        </PageContainer>
    );
}

export default WelcomePage;

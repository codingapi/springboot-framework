import React from 'react';
import {PageContainer} from "@ant-design/pro-components";
import {Button, Col, message, Row} from "antd";
import {
    Form,
    FormCaptcha,
    FormCascader,
    FormCheckbox,
    FormCode,
    FormColor,
    FormDate,
    FormInput,
    FormPassword,
    FormRadio,
    FormRate,
    FormSelect,
    FormSlider,
    FormStepper,
    FormSwitch,
    FormTextArea,
    FormUploader
} from "@codingapi/form-pc";
import {FormField, FormInstance} from "@codingapi/ui-framework";


const FooterButtons: React.FC<{ formInstance: FormInstance }> = ({formInstance}) => {
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
            select: '1-1-1,2',
            avatar: 'c84fb304c180f61bb7db40efef7f85b7',
            color: '#000000',
            ideCode: 'console.log("hello world")'
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
                    const name = formInstance.getFieldValue(["user", "name"])
                    message.success(name);
                }}
            >获取姓名</Button>

            <Button
                onClick={async () => {
                    formInstance.setFieldValue(["user", "name"], "123")
                }}
            >设置姓名</Button>

            <Button
                onClick={async () => {
                    const result = await formInstance.validate();
                    if (result) {
                        message.success("验证通过");
                    } else {
                        message.error("验证失败");
                    }
                }}
            >验证表单</Button>

            <Button
                onClick={async () => {
                    await formInstance.submit();
                }}
            >提交表单</Button>

            <Button
                onClick={async () => {
                    const values = formInstance.getFieldsValue();
                    message.success(JSON.stringify(values));
                }}
            >获取表单值</Button>

            <Button
                onClick={async () => {
                    formInstance.reset();
                }}
            >重置表单</Button>

            <Button
                onClick={async () => {
                    formInstance.setFieldsValue(data);
                }}
            >表单赋值</Button>
            <div></div>

            <Button
                onClick={async () => {
                    formInstance.enable(["user", "name"]);
                }}
            >启用姓名字段</Button>

            <Button
                onClick={async () => {
                    formInstance.disable(["user", "name"]);
                }}
            >禁用姓名字段</Button>


            <Button
                onClick={async () => {
                    formInstance.hidden(["user", "name"]);
                }}
            >隐藏姓名字段</Button>

            <Button
                onClick={async () => {
                    formInstance.show(["user", "name"]);
                }}
            >展示姓名字段</Button>

            <Button
                onClick={async () => {
                    formInstance.remove(["user", "name"]);
                }}
            >删除姓名字段</Button>

            <Button
                onClick={async () => {
                    formInstance.create({
                        props: {
                            required: true,
                            name: ['user', 'name'],
                            label: '姓名',
                            placeholder: '请输入姓名',
                        },
                        type: 'input'
                    }, 0);
                }}
            >添加姓名字段</Button>

        </div>
    )
}

const FormPage = () => {
    const leftFormInstance = Form.useForm();
    const rightFormInstance = Form.useForm();

    const fields = [
        {
            type: 'input',
            props: {
                required: true,
                name: ['user', 'name'],
                label: '姓名',
                placeholder: '请输入姓名',
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
                    {
                        label: '选项1', value: '1',
                        children: [
                            {
                                label: '选项1-1',
                                value: '1-1',
                                children: [
                                    {label: '选项1-1-1', value: '1-1-1'},
                                    {label: '选项1-1-2', value: '1-1-2'},
                                ]
                            },
                            {label: '选项1-2', value: '1-2'},
                        ]
                    },
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
        {
            type: 'color',
            props: {
                required: true,
                name: ['user', 'color'],
                label: '颜色',
            }
        },
        {
            type: 'code',
            props: {
                required: true,
                name: ['user', 'ideCode'],
                label: '代码',
            }
        },
    ] as FormField[];

    return (
        <PageContainer>
            <Row gutter={[24, 24]}>
                <Col span={12}>
                    <Form
                        form={leftFormInstance}
                        layout={"horizontal"}
                        onFinish={async (values) => {
                            message.success(JSON.stringify(values));
                        }}
                        footer={(
                            <FooterButtons
                                formInstance={leftFormInstance}
                            />
                        )}
                    >
                        <Form.Item
                            required={true}
                            name={["user", "name"]}
                            label={"姓名"}
                            rules={[
                                {
                                    required: true,
                                    message: '姓名不能为空'
                                }
                            ]}
                        >
                            <FormInput
                                placeholder={"请输入姓名"}
                            />
                        </Form.Item>

                        <Form.Item
                            required={true}
                            name={["user", "age"]}
                            label={"年龄"}
                        >
                            <FormStepper
                                placeholder={"请输入年龄"}
                            />
                        </Form.Item>


                        <Form.Item
                            required={true}
                            name={["user", "password"]}
                            label={"银行卡密码"}
                        >
                            <FormPassword/>
                        </Form.Item>

                        <Form.Item
                            required={true}
                            name={["user", "code"]}
                            label={"银行卡验证码"}
                        >
                            <FormCaptcha
                                placeholder={"请输入银行卡验证码"}
                                onCaptchaRefresh={async () => {
                                    console.log('refresh captcha')
                                    return {
                                        url: '/captcha.jpeg',
                                        code: '123'
                                    }
                                }}
                            />
                        </Form.Item>

                        <Form.Item
                            required={true}
                            name={["user", "checkbox"]}
                            label={"复选框"}
                        >
                            <FormCheckbox
                                options={[
                                    {label: '选项1', value: '1'},
                                    {label: '选项2', value: '2'},
                                    {label: '选项3', value: '3'},
                                ]}
                            />
                        </Form.Item>

                        <Form.Item
                            required={true}
                            name={["user", "radio"]}
                            label={"单选框"}
                        >
                            <FormRadio
                                options={[
                                    {label: '选项1', value: '1'},
                                    {label: '选项2', value: '2'},
                                    {label: '选项3', value: '3'},
                                ]}
                            />
                        </Form.Item>

                        <Form.Item
                            required={true}
                            name={["user", "rate"]}
                            label={"评分"}
                        >
                            <FormRate/>
                        </Form.Item>

                        <Form.Item
                            required={true}
                            name={["user", "slider"]}
                            label={"滑块"}
                        >
                            <FormSlider
                                sliderPopover={true}
                            />
                        </Form.Item>

                        <Form.Item
                            required={true}
                            name={["user", "switch"]}
                            label={"开关"}
                        >
                            <FormSwitch/>
                        </Form.Item>

                        <Form.Item
                            required={true}
                            name={["user", "textarea"]}
                            label={"文本域"}
                        >
                            <FormTextArea/>
                        </Form.Item>

                        <Form.Item
                            required={true}
                            name={["user", "date"]}
                            label={"日期"}
                        >
                            <FormDate/>
                        </Form.Item>

                        <Form.Item
                            required={true}
                            name={["user", "cascader"]}
                            label={"级联选择"}
                        >
                            <FormCascader
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
                        </Form.Item>

                        <Form.Item
                            required={true}
                            name={["user", "select"]}
                            label={"选择器"}
                        >
                            <FormSelect

                                selectMultiple={true}
                                options={[
                                    {
                                        label: '选项1', value: '1',
                                        children: [
                                            {
                                                label: '选项1-1',
                                                value: '1-1',
                                                children: [
                                                    {label: '选项1-1-1', value: '1-1-1'},
                                                    {label: '选项1-1-2', value: '1-1-2'},
                                                ]
                                            },
                                            {label: '选项1-2', value: '1-2'},
                                        ]
                                    },
                                    {label: '选项2', value: '2'},
                                    {label: '选项3', value: '3'},
                                ]}
                            />
                        </Form.Item>

                        <Form.Item
                            required={true}
                            name={["user", "avatar"]}
                            label={"头像"}
                        >
                            <FormUploader/>
                        </Form.Item>

                        <Form.Item
                            required={true}
                            name={["user", "color"]}
                            label={"颜色"}
                        >
                            <FormColor/>
                        </Form.Item>

                        <Form.Item
                            required={true}
                            name={["user", "ideCode"]}
                            label={"代码"}
                        >
                            <FormCode/>
                        </Form.Item>
                    </Form>
                </Col>

                <Col span={12}>
                    <Form
                        onFinish={async (values) => {
                            message.success(JSON.stringify(values));
                        }}
                        form={rightFormInstance}
                        footer={(
                            <FooterButtons
                                formInstance={rightFormInstance}
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

export default FormPage;

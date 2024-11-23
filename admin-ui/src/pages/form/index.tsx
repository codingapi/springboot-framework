import React from 'react';
import Page from "@/components/Layout/Page";
import {Button, Form, Select, Space} from "antd";

const FormTest = () => {

    const [form] = Form.useForm();

    return (
        <Page enablePageContainer={true}>
            <div className="App">
                <Form
                    form={form}
                    onFinish={async (values)=>{
                        console.log(values);
                    }}
                >
                    <Form.Item
                        name={"test"}
                        label={"测试"}
                        normalize={(value)=>{
                            // 自定义格式化值的方式
                            return value.join(',');
                        }}
                        getValueProps={(value)=>{
                            // 自定义设置值的方式，这里是将字符串转为数组
                            if(value) {
                                const arr = value.split(',');
                                return {
                                    value: arr
                                };
                            }
                            return value;
                        }}
                    >
                        {/*
                            对于下属组件，需要提供以下属性：
                            value: 1
                            onChange: (value: any) => void
                            通过这两个属性就可以实现双向绑定
                        */}
                        <Select
                            mode={"multiple"}
                            options={[
                                {
                                    key: '1',
                                    label: '测试1',
                                    value: '1'
                                },
                                {
                                    key: '2',
                                    label: '测试2',
                                    value: '2'
                                },
                            ]}
                        />
                    </Form.Item>
                </Form>

                <Space>
                    <Button
                        onClick={()=>{
                            form.submit();
                        }}
                    >
                        submit1
                    </Button>

                    <Button
                        onClick={()=>{
                            const values = form.getFieldsValue();
                            console.log(values);
                        }}
                    >
                        submit2
                    </Button>

                    <Button
                        onClick={()=>{
                            form.validateFields().then(res=>{
                                console.log(res);
                            }).catch(e=>{
                                console.log(e);
                            });
                        }}
                    >
                        submit3
                    </Button>

                    <Button
                        onClick={()=>{
                            form.setFieldValue('test', '1,2');
                        }}
                    >
                        setFieldValue
                    </Button>
                </Space>
            </div>
        </Page>
    );
}

export default FormTest;

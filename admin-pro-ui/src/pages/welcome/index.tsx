import React from 'react';
import './index.scss';
import {Button, Form, Input} from "antd";
import {FlowFormApiContext, FlowFormCustomValidateContext} from "@/api/validate";
import * as api from "@/api"
import {PageContainer} from "@ant-design/pro-components";

FlowFormApiContext.getInstance().setApi({
    get: (url: string, params?: any) => {
        return api.get(url, params);
    },
    post: (url: string, data: any) => {
        return api.post(url, data);
    }
});

const Index = () => {

    const [form] = Form.useForm();

    const context = new FlowFormCustomValidateContext();

    const validateFuncCode = `
         if (content.value) {
            return [];
         } else {
            return ["姓名不存在"];
        }
    `;
    context.addCustomFunctionCodeValidate(["user", "name"], validateFuncCode);

    return (
        <PageContainer>

            <Form
                form={form}
            >

                <Form.Item
                    name={["user", "name"]}
                    label={"姓名"}
                >
                    <Input/>
                </Form.Item>
            </Form>

            <Button onClick={() => {
                form.validateFields().then(res => {
                    console.log(res);
                })
            }}>test1</Button>

            <Button onClick={() => {
                context.validate(form);
            }}>test2</Button>

        </PageContainer>
    );
}

export default Index;

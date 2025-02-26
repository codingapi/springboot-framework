import React from 'react';
import {Form, Input} from "antd";
import {PageContainer} from "@ant-design/pro-components";
import './index.scss';

const WelcomePage = () => {

    const [form] = Form.useForm();

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

        </PageContainer>
    );
}

export default WelcomePage;

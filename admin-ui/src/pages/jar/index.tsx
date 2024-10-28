import React from "react";
import {PageContainer, ProForm, ProFormText} from "@ant-design/pro-components";
import ProFormUploader from "@/components/Form/ProFormUploader";
import {restart, upload} from "@/api/jar";
import {Button, Space} from "antd";

const JarPage = () => {

    const [form] = ProForm.useForm();

    return (
        <PageContainer>

            <ProForm
                form={form}
                initialValues={{
                    classname: "com.codingapi.jar.controller.HelloController"
                }}
                submitter={false}
                onFinish={async (values) => {
                    const data = values.content[0].response;
                    const filename = values.content[0].name;
                    await upload({
                        content: data,
                        filename
                    });
                }}
            >
                <ProFormText
                    name={"filename"}
                    hidden={true}
                />

                <ProFormText
                    name={"classname"}
                    label={"类名"}
                />

                <ProFormUploader
                    name={"content"}
                    label={"Jar包"}
                />

            </ProForm>

            <Space>
                <Button
                    onClick={() => {
                        form.resetFields();
                        form.setFieldsValue({
                            classname: "com.codingapi.jar.controller.HelloController"
                        });
                    }}>重置</Button>
                <Button
                    type={"primary"}
                    onClick={() => {
                        form.submit();
                    }}>提交</Button>
                <Button
                    type={"primary"}
                    danger={true}
                    onClick={async () => {
                        await restart();
                    }}
                >重启服务</Button>
            </Space>


        </PageContainer>
    )
}

export default JarPage;

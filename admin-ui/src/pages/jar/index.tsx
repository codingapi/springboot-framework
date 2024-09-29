import React, {useEffect} from "react";
import {PageContainer, ProForm, ProFormText} from "@ant-design/pro-components";
import ProFormUploader from "@/components/Form/ProFormUploader";
import {restart, upload} from "@/api/jar";
import {Button} from "antd";

const JarPage = () => {

    const [form] = ProForm.useForm();

    useEffect(() => {
        form.setFieldsValue({
            classname: "com.codingapi.jar.controller.HelloController"
        });
    }, []);

    return (
        <PageContainer>

            <ProForm
                form={form}
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

            <Button
                onClick={async () => {
                    await restart();
                }}
            >重启服务</Button>

        </PageContainer>
    )
}

export default JarPage;

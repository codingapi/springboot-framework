import React from "react";
import {PageContainer, ProForm, ProFormText} from "@ant-design/pro-components";
import ProFormUploader from "@/components/Form/ProFormUploader";
import {upload} from "@/api/jar";

const JarPage = ()=>{

    return (
        <PageContainer>

            <ProForm
                onFinish={async (values)=>{
                    const data = values.content[0].response;
                    const filename = values.content[0].name;
                    await upload({
                        content:data,
                        filename
                    });
                }}
            >
                <ProFormText
                    name={"filename"}
                    hidden={true}
                />

                <ProFormUploader
                    name={"content"}
                    label={"Jar包"}
                />

            </ProForm>

        </PageContainer>
    )
}

export default JarPage;

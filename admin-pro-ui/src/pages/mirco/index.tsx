import React, {useState} from "react";
import {Button, message, Space} from "antd";
import ComponentBus from "@/framework/ComponentBus";
import {HeaderProps} from "@/gateway";
import HeaderDefault from "@/gateway/default/Header";
import {ModalForm, ProForm, ProFormText} from "@ant-design/pro-components";


const MircoPage = () => {

    // 用于重新渲染界面
    const [pageVersion, setPageVersion] = useState(0);

    const HeaderKey = "Header";
    const Header = ComponentBus.getInstance().getComponent<HeaderProps>(HeaderKey, HeaderDefault);

    const [visible, setVisible] = useState(false);

    const [form] = ProForm.useForm();

    const handlerLoadComponent = async (values: any) => {
        const {remoteUrl, scope, module} = values;
        ComponentBus.getInstance()
            .registerRemoteComponent(HeaderKey, remoteUrl, scope, module)
            .catch(error=>{
                console.error("load remote component error", error);
                message.error('load remote component error');
            })
            .finally(() => {
                setVisible(false);
            });
    }

    return (
        <>
            <div style={{
                textAlign:'center'
            }}>
                <Space>
                    <Button onClick={() => {
                        form.setFieldsValue({
                            remoteUrl: "http://localhost:3000/remoteEntry.js",
                            scope: "MircoApp",
                            module: "./Header"
                        })
                        setVisible(true);
                    }}>load remote component</Button>

                    <Button onClick={() => {
                        ComponentBus.getInstance().removeComponent(HeaderKey);
                        setPageVersion(Math.random());
                    }}>remove remote component</Button>
                </Space>
            </div>

            <ModalForm
                title={"load remote component"}
                open={visible}
                form={form}
                modalProps={{
                    onCancel: () => {
                        setVisible(false);
                    },
                    destroyOnClose: true
                }}
                onFinish={handlerLoadComponent}
            >


                <ProFormText
                    label={"remoteUrl"}
                    name={"remoteUrl"}
                    rules={[
                        {
                            required: true,
                            message: "remoteUrl is required"
                        }
                    ]}
                />

                <ProFormText
                    label={"scope"}
                    name={"scope"}
                    rules={[
                        {
                            required: true,
                            message: "scope is required"
                        }
                    ]}
                />

                <ProFormText
                    label={"module"}
                    name={"module"}
                    rules={[
                        {
                            required: true,
                            message: "module is required"
                        }
                    ]}
                />

            </ModalForm>

            {Header && (
                <Header
                    title={"Remote Component Header"}
                    onClick={() => {
                        alert('click');
                    }}
                />
            )}

        </>
    )
}

export default MircoPage;

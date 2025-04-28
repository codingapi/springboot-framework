import React, {useState} from "react";
import {Button, Space, Toast} from "antd-mobile";
import {Access, ComponentBus} from "@codingapi/ui-framework";
import {HeaderProps} from "@/gateway";
import HeaderDefault from "@/gateway/default/Header";
import Header from "@/layout/Header";
import {Form, FormInput} from "@codingapi/form-mobile";
import Popup from "@/components/popup";


const MircoPage = () => {

    // 用于重新渲染界面
    const [pageVersion, setPageVersion] = useState(0);

    const [visible, setVisible] = useState(false);

    const HeaderKey = "Header";
    const HeaderComponent = ComponentBus.getInstance().getComponent<HeaderProps>(HeaderKey, HeaderDefault);

    const form = Form.useForm();

    const refreshPage = ()=>{
        setPageVersion(Math.random());
    }

    const handlerLoadComponent = async (values: any) => {
        const {remoteUrl, scope, module} = values;
        ComponentBus.getInstance()
            .registerRemoteComponent(HeaderKey, remoteUrl, scope, module)
            .catch(error=>{
                console.error("load remote component error", error);
                Toast.show('load remote component error');
            })
            .finally(() => {
                setVisible(false);
                refreshPage();
            });
    }

    return (
        <>
            <Header>微前端</Header>

            <Space>
                <Button onClick={() => {
                    form.setFieldsValue({
                        remoteUrl: "http://localhost:3000/remoteEntry.js",
                        scope: "MircoApp",
                        module: "./Header"
                    })
                    setVisible(true);
                }}>load remote component</Button>

                <Access hasRoles={['admin']}>
                    <Button onClick={() => {
                        ComponentBus.getInstance().removeComponent(HeaderKey);
                        refreshPage();
                    }}>remove remote component</Button>
                </Access>
            </Space>

            {HeaderComponent && (
                <HeaderComponent
                    title={"Remote Component Header"}
                    onClick={() => {
                        alert('click');
                    }}
                />
            )}

            <Popup
                bodyStyle={{
                    height: '60vh',
                }}
                title={"加载远程组件"}
                visible={visible}
                setVisible={setVisible}
                onOk={async ()=>{
                    await form.submit();
                }}
                onCancel={()=>{
                    setVisible(false);
                }}
            >
                <Form
                    form={form}
                    layout={"horizontal"}
                    onFinish={async (values)=>{
                        await handlerLoadComponent(values);
                    }}
                >
                    <FormInput name={"remoteUrl"} label={"远程地址"}/>
                    <FormInput name={"scope"} label={"作用域"}/>
                    <FormInput name={"module"} label={"模块"}/>
                </Form>
            </Popup>
        </>
    )
}

export default MircoPage;

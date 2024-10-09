import React from "react";
import {DrawerForm, ProForm, ProFormSelect, ProFormText} from "@ant-design/pro-components";
import {Button, Divider, Space} from "antd";

interface SettingPanelProps {
    visible: boolean;
    setVisible: (visible: boolean) => void;
    properties: any;
    onSettingChange: (values: any) => void;
}

const OverSettingPanel: React.FC<SettingPanelProps> = (props) => {

    const [form] = ProForm.useForm();

    return (
        <DrawerForm
            form={form}
            layout={"vertical"}
            initialValues={props.properties}
            title={"节点设置"}
            width={"40%"}
            onFinish={async (values) => {
                props.onSettingChange(values);
                props.setVisible(false);
            }}
            submitter={false}
            drawerProps={{
                onClose: () => {
                    props.setVisible(false);
                },
                destroyOnClose: true,
                extra: (
                    <Space>
                        <Button
                            type={"primary"}
                            onClick={() => {
                                form.submit();
                            }}
                        >确认</Button>
                        <Button
                            onClick={() => {
                                form.resetFields();
                                props.setVisible(false);
                            }}
                        >取消</Button>
                    </Space>
                ),
                footer: false
            }}
            open={props.visible}
        >
            <Divider>
                基本信息
            </Divider>
            <ProFormText
                name={"name"}
                label={"节点名称"}
                rules={[
                    {
                        required: true,
                        message: "请输入节点名称"
                    }
                ]}
            />

            <ProFormText
                name={"code"}
                label={"节点编码"}
                disabled={true}
                rules={[
                    {
                        required: true,
                        message: "请输入节点编码"
                    }
                ]}
            />

            <ProFormText
                name={"view"}
                label={"试图名称"}
                rules={[
                    {
                        required: true,
                        message: "请输入试图名称"
                    }
                ]}
            />

        </DrawerForm>
    )

}

export default OverSettingPanel;

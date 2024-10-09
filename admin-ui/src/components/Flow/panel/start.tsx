import React from "react";
import {DrawerForm, ProForm, ProFormText} from "@ant-design/pro-components";
import {Button, Divider, Space} from "antd";

interface SettingPanelProps {
    visible: boolean;
    setVisible: (visible: boolean) => void;
    properties: any;
    onSettingChange: (values: any) => void;
}

const StartSettingPanel: React.FC<SettingPanelProps> = (props) => {

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

            <Divider>
                节点配置
            </Divider>

            <ProFormText
                name={"outTrigger"}
                label={"出口设置"}
            />

            <ProFormText
                name={"outOperatorMatcher"}
                label={"操作人员"}
            />


        </DrawerForm>
    )

}

export default StartSettingPanel;

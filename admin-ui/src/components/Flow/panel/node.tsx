import React from "react";
import {DrawerForm, ProForm, ProFormSelect, ProFormText} from "@ant-design/pro-components";
import {Button, Divider, Space} from "antd";
import ProFormCode from "@/components/Form/ProFormCode";

interface SettingPanelProps {
    visible: boolean;
    setVisible: (visible: boolean) => void;
    properties: any;
    onSettingChange: (values: any) => void;
}

const NodeSettingPanel: React.FC<SettingPanelProps> = (props) => {

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

            <ProFormSelect
                name={"type"}
                label={"节点类型"}
                rules={[
                    {
                        required: true,
                        message: "请输入节点类型"
                    }
                ]}
                options={[
                    {
                        label: "会签",
                        value: "SIGN"
                    },
                    {
                        label: "非会签",
                        value: "NO_SIGN"
                    },
                ]}
            />

            <Divider>
                节点配置
            </Divider>

            <ProFormCode
                name={"outOperatorMatcher"}
                label={"操作人员"}
            />

            <ProFormCode
                name={"outTrigger"}
                label={"出口设置"}
            />

            <Divider>
                异常配置
            </Divider>

            <ProFormCode
                name={"errorOperatorMatcher"}
                label={"异常操作人员"}
            />

            <ProFormCode
                name={"errorTrigger"}
                label={"异常出口"}
            />

        </DrawerForm>
    )

}

export default NodeSettingPanel;

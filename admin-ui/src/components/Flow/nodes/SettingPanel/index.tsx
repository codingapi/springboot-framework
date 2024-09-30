import React from "react";
import {DrawerForm, ProForm, ProFormSelect, ProFormText} from "@ant-design/pro-components";
import {Button, Divider, Space} from "antd";

interface SettingPanelProps {
    visible: boolean;
    setVisible: (visible: boolean) => void;
    properties: any;
    onSettingChange: (values: any) => void;
}

const SettingPanel:React.FC<SettingPanelProps> = (props)=>{

    const [form] = ProForm.useForm();

    return (
        <DrawerForm
            form={form}
            initialValues={props.properties}
            title={"节点设置"}
            width={"40%"}
            onFinish={async (values)=>{
                props.onSettingChange(values);
                props.setVisible(false);
            }}
            submitter={false}
            drawerProps={{
                onClose: ()=>{
                    props.setVisible(false);
                },
                destroyOnClose:true,
                extra:(
                    <Space>
                        <Button
                            type={"primary"}
                            onClick={()=>{
                                form.submit();
                            }}
                        >确认</Button>
                        <Button
                            onClick={()=>{
                                form.resetFields();
                                props.setVisible(false);
                            }}
                        >取消</Button>
                    </Space>
                ),
                footer:false
            }}
            open={props.visible}
        >
            <Divider>
                基本信息
            </Divider>
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

            <Divider>
                节点配置
            </Divider>

            <ProFormText
                name={"outTrigger"}
                label={"出口设置"}
                rules={[
                    {
                        required: true,
                        message: "请输入节点出口"
                    }
                ]}
            />

            <ProFormText
                name={"outOperatorMatcher"}
                label={"操作人员"}
                rules={[
                    {
                        required: true,
                        message: "请输入操作人员"
                    }
                ]}
            />

            <Divider>
                异常配置
            </Divider>

            <ProFormText
                name={"errorTrigger"}
                label={"异常出口"}
            />

            <ProFormText
                name={"errorOperatorMatcher"}
                label={"异常操作人员"}
            />


        </DrawerForm>
    )

}

export default SettingPanel;

import React from "react";
import {DrawerForm, ProForm, ProFormDigit, ProFormSelect, ProFormSwitch, ProFormText} from "@ant-design/pro-components";
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
                tooltip={"界面渲染试图的名称"}
                rules={[
                    {
                        required: true,
                        message: "请输入试图名称"
                    }
                ]}
            />

            <Divider>
                节点配置
            </Divider>

            <ProFormSelect
                name={"approvalType"}
                label={"节点类型"}
                tooltip={"会签即多人审批以后再处理，非会签则是一个人处理以后即可响应"}
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
                        value: "UN_SIGN"
                    },
                ]}
            />

            <ProFormCode
                tooltip={"操作人匹配脚本"}
                name={"operatorMatcher"}
                label={"操作人"}
            />

            <ProFormDigit
                tooltip={"超时提醒时间，单位毫米。为0时则为无超时设置"}
                fieldProps={{
                    step:1
                }}
                name={"timeout"}
                label={"超时时间"}
            />

            <ProFormSwitch
                tooltip={"关闭编辑以后在当前节点下的流程表单无法修改数据"}
                name={"editable"}
                label={"是否编辑"}
            />

            <ProFormCode
                tooltip={"待办记录中的标题生成器脚本"}
                name={"titleGenerator"}
                label={"自定义标题"}
            />

            <Divider>
                异常配置
            </Divider>

            <ProFormCode
                tooltip={"当节点无人员匹配时的异常补偿脚本，可以指定人员或节点处理"}
                name={"errTrigger"}
                label={"异常配置"}
            />



        </DrawerForm>
    )

}

export default NodeSettingPanel;

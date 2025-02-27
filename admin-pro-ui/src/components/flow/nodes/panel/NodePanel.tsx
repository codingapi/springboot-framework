import React from "react";
import {Button, Divider, Space} from "antd";
import {ProForm, ProFormDigit, ProFormSelect, ProFormSwitch, ProFormText} from "@ant-design/pro-components";
import {EyeOutlined, SettingOutlined} from "@ant-design/icons";
import GroovyScript from "@/components/flow/utils/script";
import ScriptModal from "@/components/flow/nodes/panel/ScriptModal";
import {getComponent} from "@/framework/ComponentBus";
import {UserSelectProps, UserSelectViewKey} from "@/components/flow/flow/types";

interface NodePanelProps {
    id?: string,
    data?: any,
    onFinish: (values: any) => void,
    form: any,
    type: string,
}

const NodePanel: React.FC<NodePanelProps> = (props) => {

    const [form] = ProForm.useForm();

    const [visible, setVisible] = React.useState(false);

    const [userSelectVisible, setUserSelectVisible] = React.useState(false);

    const [operatorMatcherType, setOperatorMatcherType] = React.useState(props.data?.operatorMatcherType);

    // 用户选人视图
    const UserSelectView = getComponent(UserSelectViewKey) as React.ComponentType<UserSelectProps>;


    return (
        <>
            <ProForm
                initialValues={{
                    ...props.data,
                    operatorMatcherType: GroovyScript.operatorMatcherType(props.data?.operatorMatcher),
                    errTriggerType: GroovyScript.errTriggerType(props.data?.errTrigger),
                    titleGeneratorType: GroovyScript.titleGeneratorType(props.data?.titleGenerator),
                }}
                form={props.form}
                layout={"vertical"}
                onFinish={props.onFinish}
                submitter={false}
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
                    disabled={props.type === 'start' || props.type === 'over'}
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
                    label={"视图名称"}
                    tooltip={"界面渲染视图的名称"}
                    rules={[
                        {
                            required: true,
                            message: "请输入视图名称"
                        }
                    ]}
                />

                <Divider>
                    节点配置
                </Divider>

                <ProFormSelect
                    name={"approvalType"}
                    label={"节点类型"}
                    hidden={props.type !== 'node'}
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

                <ProFormText
                    tooltip={"操作人匹配脚本"}
                    name={"operatorMatcher"}
                    label={"操作人"}
                    hidden={true}
                />

                <ProFormSelect
                    tooltip={"操作人匹配脚本"}
                    name={"operatorMatcherType"}
                    label={"操作人"}
                    width={"lg"}
                    options={[
                        {
                            label: "任意人",
                            value: "any"
                        },
                        {
                            label: "发起人",
                            value: "creator"
                        },
                        {
                            label: "自定义",
                            value: "custom"
                        },
                    ]}
                    onChange={(value) => {
                        setOperatorMatcherType(value as string);
                        props.form.setFieldsValue({
                            operatorMatcher: GroovyScript.operatorMatcher(value as string)
                        })
                    }}
                    addonAfter={(
                        <Space>
                            {operatorMatcherType==='custom' && (
                                <Button
                                    icon={<SettingOutlined/>}
                                    onClick={() => {
                                        setUserSelectVisible(true);
                                    }}
                                >
                                    选择人员
                                </Button>
                            )}

                            <EyeOutlined
                                onClick={() => {
                                    const value = props.form.getFieldValue("operatorMatcher");
                                    form.setFieldValue("type", "operatorMatcher");
                                    form.setFieldValue("script", value);
                                    setVisible(true);
                                }}/>

                        </Space>
                    )}
                />

                <ProFormDigit
                    tooltip={"超时提醒时间，单位毫米。为0时则为无超时设置"}
                    fieldProps={{
                        step: 1
                    }}
                    name={"timeout"}
                    hidden={props.type === 'circulate'}
                    label={"超时时间"}
                />

                <ProFormSwitch
                    tooltip={"关闭编辑以后在当前节点下的流程表单无法修改数据"}
                    name={"editable"}
                    label={"是否编辑"}
                />

                <ProFormText
                    name={"titleGenerator"}
                    label={"自定义标题"}
                    hidden={true}
                />

                <ProFormSelect
                    tooltip={"待办记录中的标题生成器脚本"}
                    name={"titleGeneratorType"}
                    label={"自定义标题"}
                    width={"lg"}
                    options={[
                        {
                            label: "默认",
                            value: "default"
                        },
                        {
                            label: "自定义",
                            value: "custom"
                        },
                    ]}
                    onChange={(value) => {
                        if (value === "default") {
                            props.form.setFieldsValue({
                                titleGenerator: GroovyScript.defaultTitleGenerator
                            })
                        }
                    }}
                    addonAfter={(
                        <EyeOutlined
                            onClick={() => {
                                const value = props.form.getFieldValue("titleGenerator");
                                form.setFieldValue("type", "titleGenerator");
                                form.setFieldValue("script", value);
                                setVisible(true);
                            }}/>
                    )}
                />

                <Divider>
                    异常配置
                </Divider>

                <ProFormText
                    name={"errTrigger"}
                    label={"异常配置"}
                    hidden={true}
                />

                <ProFormSelect
                    tooltip={"当节点无人员匹配时的异常补偿脚本，可以指定人员或节点处理"}
                    name={"errTriggerType"}
                    label={"异常配置"}
                    width={"lg"}
                    options={[
                        {
                            label: "默认",
                            value: "default"
                        },
                        {
                            label: "自定义",
                            value: "custom"
                        },
                    ]}
                    onChange={(value) => {
                        if (value === "default") {
                            props.form.setFieldsValue({
                                errTrigger: GroovyScript.defaultOutTrigger
                            })
                        }
                    }}
                    addonAfter={(
                        <EyeOutlined
                            onClick={() => {
                                const value = props.form.getFieldValue("errTrigger");
                                form.setFieldValue("type", "errTrigger");
                                form.setFieldValue("script", value);
                                setVisible(true);
                            }}/>
                    )}
                />

            </ProForm>

            <ScriptModal
                onFinish={(values) => {
                    const type = values.type;
                    props.form.setFieldsValue({
                        [type]: values.script
                    });
                }}
                form={form}
                setVisible={setVisible}
                visible={visible}/>

            {UserSelectView && (
                <UserSelectView
                    visible={userSelectVisible}
                    setVisible={setUserSelectVisible}
                    userSelectType={"users"}
                    specifyUserIds={GroovyScript.getOperatorUsers(props.form.getFieldValue("operatorMatcher"))}
                    mode={"multiple"}
                    onFinish={(values) => {
                        setUserSelectVisible(false);
                        const script = GroovyScript.specifyOperatorMatcher.replaceAll("%s", values.map((item: any) => item.id).join(","));
                        props.form.setFieldsValue({
                            operatorMatcher: script
                        });
                    }}
                />
            )}

        </>
    )
}

export default NodePanel;



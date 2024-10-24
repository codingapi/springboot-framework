import React from "react";
import {Divider} from "antd";
import {ProForm, ProFormDigit, ProFormSelect, ProFormSwitch, ProFormText} from "@ant-design/pro-components";
import {EyeOutlined} from "@ant-design/icons";
import GroovyScript from "@/components/Flow/utils/script";
import ScriptModal from "@/components/Flow/panel/ScriptModal";

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
                    disabled={props.type !== 'node'}
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
                        props.form.setFieldsValue({
                            operatorMatcher: GroovyScript.operatorMatcher(value as string)
                        })
                    }}
                    addonAfter={(
                        <EyeOutlined
                            onClick={() => {
                                const value = props.form.getFieldValue("operatorMatcher");
                                form.setFieldValue("type", "operatorMatcher");
                                form.setFieldValue("script", value);
                                setVisible(true);
                            }}/>
                    )}
                />

                <ProFormDigit
                    tooltip={"超时提醒时间，单位毫米。为0时则为无超时设置"}
                    fieldProps={{
                        step: 1
                    }}
                    name={"timeout"}
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
                onFinish={(values)=>{
                    const type = values.type;
                    props.form.setFieldsValue({
                        [type]: values.script
                    });
                }}
                form={form}
                setVisible={setVisible}
                visible={visible}/>
        </>
    )
}

export default NodePanel;



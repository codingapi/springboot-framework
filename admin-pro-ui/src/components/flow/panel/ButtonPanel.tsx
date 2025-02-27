import React from "react";
import {
    ActionType,
    ModalForm,
    ProColumns,
    ProForm,
    ProFormColorPicker,
    ProFormDigit,
    ProFormSelect,
    ProFormText,
    ProTable
} from "@ant-design/pro-components";
import {Button, ColorPicker, Popconfirm, Space} from "antd";
import FlowUtils from "@/components/flow/utils";
import ScriptModal from "@/components/flow/panel/ScriptModal";
import {EyeOutlined, ReloadOutlined} from "@ant-design/icons";
import {CustomButtonType} from "@/components/flow/flow/types";

interface ButtonPanelProps {
    id: string;
}

const buttonEventOptions = [
    {
        label: "保存",
        value: "SAVE"
    },
    {
        label: "发起",
        value: "START"
    },
    {
        label: "提交",
        value: "SUBMIT"
    },
    {
        label: "预提交",
        value: "TRY_SUBMIT"
    },
    {
        label: "指定人员提交",
        value: "SPECIFY_SUBMIT"
    },
    {
        label: "驳回",
        value: "REJECT"
    },
    {
        label: "转办",
        value: "TRANSFER"
    },
    {
        label: "撤销",
        value: "RECALL"
    },
    {
        label: "延期",
        value: "POSTPONED"
    },
    {
        label: "催办",
        value: "URGE"
    },
    {
        label: "自定义接口",
        value: "CUSTOM"
    },
    {
        label: "自定义事件",
        value: "VIEW"
    },
    {
        label: "删除",
        value: "REMOVE"
    },
] as {
    label: string;
    value: CustomButtonType;
}[];


const ButtonPanel: React.FC<ButtonPanelProps> = (props) => {

    const actionRef = React.useRef<ActionType>();

    const [form] = ProForm.useForm();

    const [groovyForm] = ProForm.useForm();

    const [visible, setVisible] = React.useState(false);

    const [scriptVisible, setScriptVisible] = React.useState(false);

    const [type, setType] = React.useState<string>();

    const columns = [
        {
            title: 'id',
            dataIndex: 'id',
            key: 'id',
            hidden: true
        },
        {
            title: '按钮名称',
            dataIndex: 'name',
            key: 'name',
        },
        {
            title: '事件类型',
            dataIndex: 'type',
            key: 'type',
            render: (value: string) => {
                return buttonEventOptions.find((item: any) => item.value == value)?.label;
            }
        },
        {
            title: '按钮颜色',
            dataIndex: 'style',
            key: 'style',
            render: (_: any, record: any) => {
                return <ColorPicker value={record.style?.background} disabled={true}/>;
            }
        },
        {
            title: '排序',
            dataIndex: 'order',
            key: 'order',
        },
        {
            title: "操作",
            valueType: "option",
            render: (_: any, record: any) => {
                return [
                    <a
                        key={"edit"}
                        onClick={() => {
                            form.resetFields();
                            form.setFieldsValue(record);
                            setType(record.type);
                            setVisible(true);
                        }}
                    >
                        修改
                    </a>,
                    <Popconfirm
                        key={"delete"}
                        title={"确认要删除吗？"}
                        onConfirm={() => {
                            FlowUtils.deleteButton(props.id, record.id);
                            actionRef.current?.reload();
                        }}>
                        <a>删除</a>
                    </Popconfirm>
                ]
            }
        }
    ] as ProColumns[];

    return (
        <>
            <ProTable
                columns={columns}
                actionRef={actionRef}
                key={"id"}
                search={false}
                options={false}
                pagination={false}
                request={async () => {
                    const buttons = FlowUtils.getButtons(props.id);
                    return {
                        data: buttons,
                        total: buttons.length,
                    }
                }}
                toolBarRender={() => {
                    return [
                        <Button
                            type={"primary"}
                            onClick={() => {
                                form.resetFields();
                                setVisible(true);
                            }}
                        >添加按钮</Button>
                    ]
                }}
            />

            <ModalForm
                title={"添加节点按钮"}
                open={visible}
                form={form}
                modalProps={{
                    onCancel: () => {
                        setVisible(false);
                    },
                    onOk: () => {
                        setVisible(false);
                    },
                    destroyOnClose: true
                }}
                onFinish={async (values) => {
                    FlowUtils.updateButton(props.id, values);
                    setVisible(false);
                    actionRef.current?.reload();
                }}
            >
                <ProFormText
                    name={"id"}
                    hidden={true}
                />


                <ProFormText
                    name={"name"}
                    label={"按钮名称"}
                    placeholder={"请输入按钮名称"}
                    rules={[
                        {
                            required: true,
                            message: '请输入按钮名称'
                        }
                    ]}
                />

                <ProFormColorPicker
                    name={"style"}
                    label={(
                        <Space>
                            按钮颜色
                            <ReloadOutlined
                                alt={"重置"}
                                onClick={() => {
                                    form.setFieldsValue({'style': null});
                                }}
                            />
                        </Space>
                    )}
                    normalize={(value:any) => {
                        if (value) {
                            return {
                                background: value.toHexString()
                            };
                        }
                        return value;
                    }}
                    getValueProps={(value:any) => {
                        const color = value?.background;
                        if (color) {
                            return {
                                value: color
                            }
                        }
                        return value;
                    }}
                    placeholder={"请选择按钮颜色"}
                />

                <ProFormSelect
                    name={"type"}
                    label={(
                        <Space>
                            按钮类型

                            {type === 'CUSTOM' && (
                                <EyeOutlined
                                    onClick={() => {
                                        groovyForm.resetFields();
                                        const script = form.getFieldValue('groovy') || 'def run(content){\n  //你的代码 \n  return content.createMessageResult(\'我是自定义标题\');\n}';
                                        groovyForm.setFieldsValue({
                                            'script': script
                                        });
                                        setScriptVisible(!scriptVisible);
                                    }}/>
                            )}

                        </Space>
                    )}
                    placeholder={"请输入按钮类型"}
                    rules={[
                        {
                            required: true,
                            message: '请输入按钮类型'
                        }
                    ]}
                    options={buttonEventOptions}
                    onChange={(value: string) => {
                        setType(value);
                    }}
                />

                {type === 'VIEW' && (
                    <ProFormText
                        name={"eventKey"}
                        label={"事件Key"}
                        tooltip={"事件Key用于流程Form的事件触发"}
                        rules={[
                            {
                                required: true,
                                message: '请输入事件Key'
                            }
                        ]}
                    />
                )}

                <ProFormText
                    name={"groovy"}
                    hidden={true}
                />

                <ScriptModal
                    onFinish={(values) => {
                        form.setFieldsValue({
                            'groovy': values.script
                        });
                    }}
                    form={groovyForm}
                    setVisible={setScriptVisible}
                    visible={scriptVisible}/>

                <ProFormDigit
                    name={"order"}
                    label={"排序"}
                    min={0}
                    fieldProps={{
                        step: 1
                    }}
                    placeholder={"请输入排序"}
                    rules={[
                        {
                            required: true,
                            message: '请输入排序'
                        }
                    ]}
                />

            </ModalForm>
        </>
    )
}

export default ButtonPanel;

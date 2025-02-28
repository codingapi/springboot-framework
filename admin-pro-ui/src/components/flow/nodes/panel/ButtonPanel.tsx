import React from "react";
import {ActionType, ModalForm, ProColumns, ProForm, ProFormText, ProTable} from "@ant-design/pro-components";
import {Button, ColorPicker, Popconfirm, Space} from "antd";
import ScriptModal from "@/components/flow/nodes/panel/ScriptModal";
import {EyeOutlined} from "@ant-design/icons";
import FlowContext from "@/components/flow/domain/FlowContext";
import FormInput from "@/components/form/input";
import ValidateUtils from "@/components/form/utils";
import FormSelect from "@/components/form/select";
import FormColor from "@/components/form/color";

interface ButtonPanelProps {
    id: string;
}

const ButtonPanel: React.FC<ButtonPanelProps> = (props) => {

    const actionRef = React.useRef<ActionType>();

    const [form] = ProForm.useForm();
    const [groovyForm] = ProForm.useForm();


    const [visible, setVisible] = React.useState(false);

    const [scriptVisible, setScriptVisible] = React.useState(false);

    const [type, setType] = React.useState<string>();

    const flowContext = FlowContext.getInstance();

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
                return flowContext.getFlowPanelContext()?.convertButtonValue(value);
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
                            groovyForm.resetFields();
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
                            flowContext.getFlowPanelContext()?.deleteButton(props.id, record.id);
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
                    const buttons = flowContext?.getFlowPanelContext()?.getButtons(props.id) || [];
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
                                groovyForm.resetFields();
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
                onFinish={async (values) => {
                    flowContext.getFlowPanelContext()?.updateButton(props.id, values as any);
                    setVisible(false);
                    actionRef.current?.reload();
                }}
                modalProps={{
                    onCancel: () => {
                        setVisible(false);
                    },
                    onClose:()=>{
                        setVisible(false);
                    },
                    destroyOnClose:true
                }}
            >
                <FormInput
                    name={"id"}
                    hidden={true}
                />

                <ProFormText
                    name={"name"}
                    label={"按钮名称"}
                    placeholder={"请输入按钮名称"}
                    required={true}
                    rules={[
                        {
                            required: true,
                            message: '请输入按钮名称',
                        },
                    ]}
                />

                <FormColor
                    name={["style","background"]}
                    label={"按钮颜色"}
                    placeholder={"请输入按钮颜色"}
                    required={true}
                    validateFunction={ValidateUtils.validateNotEmpty}
                />

                <FormSelect
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
                    required={true}
                    options={flowContext.getFlowPanelContext()?.getButtonEventOptions()}
                    onChange={(value: string) => {
                        setType(value);
                    }}
                />

                {type === 'VIEW' && (
                    <FormInput
                        name={"eventKey"}
                        label={"事件Key"}
                        help={"事件Key用于流程Form的事件触发"}
                        required={true}
                    />
                )}

                <FormInput
                    name={"groovy"}
                    hidden={true}
                />

                <FormInput
                    name={"order"}
                    label={"排序"}
                    inputType={"number"}
                    placeholder={"请输入排序"}
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

            </ModalForm>
        </>
    )
}

export default ButtonPanel;

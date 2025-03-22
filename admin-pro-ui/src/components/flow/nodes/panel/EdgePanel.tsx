import React from "react";
import {ActionType, ProForm, ProTable} from "@ant-design/pro-components";
import {Input, InputNumber, Popconfirm, Space} from "antd";
import {CheckOutlined, EditOutlined, SettingOutlined} from "@ant-design/icons";
import ScriptModal from "@/components/flow/nodes/panel/ScriptModal";
import FlowContext from "@/components/flow/domain/FlowContext";

interface EdgePanelProps {
    id?: string;
    type: string;
}

const EdgePanel: React.FC<EdgePanelProps> = (props) => {

    const [visible, setVisible] = React.useState(false);

    const [name, setName] = React.useState("");
    const [order, setOrder] = React.useState(0);

    const [groovyForm] = ProForm.useForm();
    const actionRef = React.useRef<ActionType>();

    const flowContext = FlowContext.getInstance();

    const handlerChangeName = (id: any) => {
        flowContext.getFlowPanelContext()?.changeEdgeName(id, name);
        actionRef.current?.reload();
    }

    const handlerChangeOrder = (id: any) => {
        flowContext.getFlowPanelContext()?.changeEdgeOrder(id, order);
        actionRef.current?.reload();
    }

    const handlerChangeBack = (id: any, back: boolean) => {
        flowContext.getFlowPanelContext()?.changeEdgeBack(id, back);
        actionRef.current?.reload();
    }

    const handlerChangeOutTrigger = (id: any, outTrigger: string) => {
        flowContext.getFlowPanelContext()?.changeEdgeOutTrigger(id, outTrigger);
        actionRef.current?.reload();
    }

    const columns = [
        {
            title: 'id',
            dataIndex: 'id',
            key: 'id',
            hidden: true
        },
        {
            title: '关系名称',
            dataIndex: 'name',
            key: 'name',
            render: (text: string, record: any) => {
                return (
                    <Space>
                        {record.name ? record.name : "未命名"}

                        <Popconfirm
                            title={"修改名称"}
                            description={(
                                <Input
                                    defaultValue={record.name}
                                    onChange={(e) => {
                                        setName(e.target.value);
                                    }}/>
                            )}
                            onConfirm={() => {
                                handlerChangeName(record.id);
                            }}
                        >
                            <EditOutlined/>
                        </Popconfirm>
                    </Space>
                )
            }
        },
        {
            title: '关系类型',
            dataIndex: 'type',
            key: 'relation',
            render: (text: string, record: any) => {
                return (
                    <>{record.source.name} {"->"} {record.target.name}</>
                )
            }
        },
        {
            title: '出口设置',
            dataIndex: 'outTrigger',
            key: 'outTrigger',
            render: (text: string, record: any) => {
                return (
                    <Space>
                        <SettingOutlined
                            onClick={() => {
                                groovyForm.setFieldValue("script", record.outTrigger);
                                groovyForm.setFieldValue("type", record.id);
                                setVisible(true);
                            }}/>
                        {record.outTrigger ? (<CheckOutlined/>) : null}
                    </Space>
                )
            }
        },
        {
            title: '是否退回',
            dataIndex: 'back',
            hidden: props.type === 'start',
            key: 'back',
            render: (text: string, record: any) => {
                return (

                    <Space>
                        <Popconfirm
                            title={`确认修改为${text ? '否' : '是'}吗？`}
                            onConfirm={() => {
                                handlerChangeBack(record.id, !text);
                            }}
                        >
                            <SettingOutlined/>
                        </Popconfirm>

                        {text ? '是' : '否'}
                    </Space>


                )
            }
        },
        {
            title: '排序',
            dataIndex: 'order',
            key: 'order',
            render: (text: string, record: any) => {
                return (
                    <Space>
                        <Popconfirm
                            title={"修改排序"}
                            description={(
                                <InputNumber
                                    defaultValue={record.order}
                                    step={1}
                                    onChange={(e) => {
                                        setOrder(e);
                                    }}/>)}
                            onConfirm={() => {
                                handlerChangeOrder(record.id);
                            }}
                        >
                            <SettingOutlined/>
                        </Popconfirm>

                        {text}
                    </Space>
                )
            }
        },
    ] as any[];


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
                    const data = props.id ? flowContext.getFlowPanelContext()?.getEdges(props.id) as any[] : [];
                    return {
                        data: data.sort((a: any, b: any) => {
                            return a.order - b.order;
                        }),
                        success: true
                    }
                }}
            />

            <ScriptModal
                onFinish={(values) => {
                    handlerChangeOutTrigger(values.type, values.script);
                }}
                form={groovyForm}
                setVisible={setVisible}
                visible={visible}/>
        </>
    )
}
export default EdgePanel;

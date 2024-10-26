import React from "react";
import {Button, Divider, Form, message, Modal, Row, Space, Tabs} from "antd";
import {detail, postponed, recall, saveFlow, submitFlow, transfer} from "@/api/flow";
import {
    ModalForm,
    ProDescriptions,
    ProForm,
    ProFormDigit,
    ProFormText,
    ProFormTextArea
} from "@ant-design/pro-components";
import moment from "moment";
import UserSelect from "@/pages/flow/user/select";


interface FlowFormView {
    [key: string]: React.ComponentType;
}

interface FlowViewProps {
    id: any;
    visible: boolean;
    setVisible: (visible: boolean) => void;
    view: FlowFormView;
    review?: boolean;
}

const FlowView: React.FC<FlowViewProps> = (props) => {


    const [data, setData] = React.useState<any>(null);

    const [postponedVisible, setPostponedVisible] = React.useState(false);
    const [transferVisible,setTransferVisible] = React.useState(false);
    const [selectUserVisible, setSelectUserVisible] = React.useState(false);

    const [viewForm] = Form.useForm();

    const [opinionForm] = Form.useForm();

    const [transferForm] = Form.useForm();

    const handlerSaveFlow = () => {
        const advice = opinionForm.getFieldValue('advice');
        const recordId = props.id;
        const binData = viewForm.getFieldsValue();
        const clazzName = data.flowRecord.bindClass;
        const body = {
            recordId,
            advice,
            formData: {
                ...binData,
                clazzName
            }
        }
        saveFlow(body).then(res => {
            if (res.success) {
                message.success('保存成功').then();
                props.setVisible(false);
            }
        })
    }


    const handleRecallFlow = () => {
        const recordId = props.id;
        const body = {
            recordId,
        }
        recall(body).then(res => {
            if (res.success) {
                message.success('流程已撤回').then();
                props.setVisible(false);
            }
        })
    }


    const handlePostponedFlow = (values: any) => {
        const recordId = props.id;
        const body = {
            recordId,
            timeOut: values.hours * 1000 * 60 * 60
        }
        postponed(body).then(res => {
            if (res.success) {
                message.success('已经延期').then();
                setPostponedVisible(false)
            }
        })
    }

    const handlerTransferFlow = (values:any) => {
        const advice = opinionForm.getFieldValue('advice');
        const recordId = props.id;
        const binData = viewForm.getFieldsValue();
        const clazzName = data.flowRecord.bindClass;
        const body = {
            recordId,
            advice,
            targetUserId: values.userId,
            formData: {
                ...binData,
                clazzName
            }
        }
        transfer(body).then(res => {
            if (res.success) {
                message.success('已经转办给'+values.userName).then();
                setTransferVisible(false);
                props.setVisible(false);
            }
        });
    }

    const handleSubmitFlow = (success: boolean) => {
        const advice = opinionForm.getFieldValue('advice');
        const recordId = props.id;
        const binData = viewForm.getFieldsValue();
        const clazzName = data.flowRecord.bindClass;
        const body = {
            recordId,
            advice: advice,
            success: success,
            formData: {
                ...binData,
                clazzName
            }
        }
        submitFlow(body).then(res => {
            if (res.success) {
                message.success('流程已提交').then();
                props.setVisible(false);
            }
        })
    }


    const getOperator = (id: number) => {
        if (data) {
            const operators = data.operators;
            for (const operator of operators) {
                if (operator.id === id) {
                    return operator;
                }
            }
        }
        return null;
    }

    const getNode = (code: string) => {
        if (data) {
            const nodes = data.flowWork.nodes;
            for (const node of nodes) {
                if (node.code === code) {
                    return node;
                }
            }
        }
        return null;
    }

    const handleDetail = () => {
        detail(props.id).then(res => {
            if (res.success) {
                opinionForm.setFieldValue('advice', res.data.flowRecord.opinion?.advice);
                setData(res.data);
            }
        });
    }


    React.useEffect(() => {

        if (props.visible) {
            handleDetail();
        }

    }, [props.visible]);


    const FlowView = (props: any) => {
        const {data} = props;

        const Component = props.view[getNode(data.flowRecord.nodeCode)?.view] as any;
        const form = data.bindData;

        return (
            <Component data={form} form={viewForm}/>
        )
    }

    return (
        <Modal
            title={"流程详情"}
            width={"80%"}
            open={props.visible}
            onClose={() => {
                props.setVisible(false);
            }}
            onCancel={() => {
                props.setVisible(false);
            }}
            onOk={() => {
                props.setVisible(false);
            }}
            footer={false}
        >
            <Row justify="end">
                <Space>
                    {props.review && (
                        <Button
                            type={"primary"}
                            danger={true}
                        >干预</Button>
                    )}

                    {!props.review && (
                        <Button
                            onClick={() => {
                                handlerSaveFlow();
                            }}
                        >保存</Button>
                    )}


                    {!props.review && (
                        <Button
                            type={"primary"}
                            onClick={() => {
                                handleSubmitFlow(true);
                            }}
                        >同意</Button>
                    )}


                    {!props.review && (
                        <Button
                            type={"primary"}
                            onClick={() => {
                                handleSubmitFlow(false);
                            }}
                            danger={true}
                        >拒绝</Button>
                    )}

                    {!props.review && (
                        <Button
                            type={"primary"}
                            onClick={()=>{
                                setTransferVisible(true);
                            }}
                        >转办</Button>
                    )}

                    {!props.review && (
                        <Button
                            type={"primary"}
                            onClick={() => {
                                setPostponedVisible(true);
                            }}
                            danger={true}
                        >延期</Button>
                    )}

                    {!props.review && (
                        <Button
                            type={"primary"}
                            onClick={() => {
                                handleRecallFlow();
                            }}
                            danger={true}
                        >撤销</Button>
                    )}

                    <Button
                        onClick={() => {
                            props.setVisible(false);
                        }}
                    >关闭</Button>

                </Space>
            </Row>

            {data && (
                <>
                    <Tabs
                        items={[
                            {
                                label: "详情",
                                key: "detail",
                                children: (
                                    <>
                                        <ProDescriptions column={2}>
                                            <ProDescriptions.Item
                                                span={2}
                                                label={"标题"}
                                            >
                                                {data.flowRecord.title}
                                            </ProDescriptions.Item>
                                            <ProDescriptions.Item
                                                span={1}
                                                label={"发起人"}
                                            >
                                                {getOperator(data.flowRecord.createOperatorId)?.name}
                                            </ProDescriptions.Item>
                                            <ProDescriptions.Item
                                                span={1}
                                                label={"发起时间"}
                                            >
                                                {moment(data.flowRecord.createTime).format("YYYY-MM-DD HH:mm:ss")}
                                            </ProDescriptions.Item>

                                            <ProDescriptions.Item
                                                span={1}
                                                label={"状态"}
                                            >
                                                {data.flowRecord.flowStatus === 'RUNNING' && '进行中'}
                                                {data.flowRecord.flowStatus === 'FINISH' && '已结束'}
                                            </ProDescriptions.Item>
                                            <ProDescriptions.Item
                                                span={1}
                                                label={"流程状态"}
                                            >
                                                {data.flowRecord.recodeType === 'TODO' && '待办'}
                                                {data.flowRecord.recodeType === 'DONE' && '已办'}
                                                {data.flowRecord.recodeType === 'TRANSFER' && '已转办'}
                                            </ProDescriptions.Item>
                                            <ProDescriptions.Item
                                                span={1}
                                                label={"是否延期"}
                                            >
                                                {data.flowRecord.postponedCount > 0 ? '延期' : '未延期'}
                                            </ProDescriptions.Item>
                                            <ProDescriptions.Item
                                                span={1}
                                                label={"是否干预"}
                                            >
                                                {data.flowRecord.interfere ? '干预' : '未干预'}
                                            </ProDescriptions.Item>
                                            <ProDescriptions.Item
                                                span={1}
                                                label={"是否已读"}
                                            >
                                                {data.flowRecord.read ? '已读' : '未读'}
                                            </ProDescriptions.Item>
                                            <ProDescriptions.Item
                                                span={1}
                                                label={"超时时间"}
                                            >
                                                {data.flowRecord.timeoutTime == 0 ? '未设置' : moment(data.flowRecord.timeoutTime).format("YYYY-MM-DD HH:mm:ss")}
                                            </ProDescriptions.Item>
                                            <ProDescriptions.Item
                                                span={1}
                                                label={"节点名称"}
                                            >
                                                {getNode(data.flowRecord.nodeCode)?.name}
                                            </ProDescriptions.Item>
                                        </ProDescriptions>

                                        <div>
                                            <Divider>
                                                流程表单
                                            </Divider>
                                            <FlowView data={data} view={props.view}/>
                                        </div>
                                        <div>
                                            {data.historyRecords && data.historyRecords.filter((item: any) => item.recodeType === 'DONE').length > 0 && (
                                                <>
                                                    <Divider>
                                                        审批意见
                                                    </Divider>
                                                    {data.historyRecords && data.historyRecords.filter((item: any) => item.recodeType === 'DONE').map((item: any) => {
                                                        return (
                                                            <ProDescriptions
                                                                column={2}
                                                                dataSource={item}
                                                                columns={[
                                                                    {
                                                                        title: '审批人',
                                                                        dataIndex: 'currentOperatorId',
                                                                        renderText: (text: any, record: any) => {
                                                                            return (
                                                                                <>
                                                                                    {moment(record.updateTime).format("YYYY-MM-DD HH:mm:ss")}
                                                                                    &nbsp;&nbsp;
                                                                                    {getOperator(record.currentOperatorId)?.name}
                                                                                </>
                                                                            )
                                                                        }
                                                                    },
                                                                    {
                                                                        title: '审批意见',
                                                                        dataIndex: 'opinion',
                                                                        renderText: (text: any, record: any) => {
                                                                            return (
                                                                                <>
                                                                                    {record.opinion.success ? '同意' : '拒绝'}: {record.opinion.advice}
                                                                                </>
                                                                            )
                                                                        }
                                                                    },
                                                                ]}
                                                            />
                                                        )
                                                    })}
                                                </>
                                            )}
                                        </div>


                                        <Divider>
                                            审批
                                        </Divider>
                                        <ProForm
                                            form={opinionForm}
                                            submitter={false}
                                        >
                                            <ProFormTextArea
                                                disabled={props.review}
                                                label={"审批意见"}
                                                name={"advice"}
                                            />

                                        </ProForm>

                                    </>
                                )
                            },
                            {
                                label: "流程图",
                                key: "flow",
                                children: (
                                    <></>
                                )
                            },
                            {
                                label: "流转历史",
                                key: "history",
                                children: (
                                    <></>
                                )
                            },
                        ]}
                    />
                </>
            )}

            <ModalForm
                title={"延期调整"}
                open={postponedVisible}
                modalProps={{
                    onCancel: () => {
                        setPostponedVisible(false);
                    },
                    onClose: () => {
                        setPostponedVisible(false);
                    }
                }}
                onFinish={async (values) => {
                    handlePostponedFlow(values);
                }}

            >
                <ProFormDigit
                    name={"hours"}
                    label={"延期时间"}
                    rules={[
                        {
                            required: true,
                            message: "请输入延期时间"
                        }
                    ]}
                />
            </ModalForm>


            <ModalForm
                title={"延期人员选择"}
                form={transferForm}
                open={transferVisible}
                modalProps={{
                    onCancel: () => {
                        setTransferVisible(false);
                    },
                    onClose: () => {
                        setTransferVisible(false);
                    }
                }}
                onFinish={async (values) => {
                    handlerTransferFlow(values);
                }}
            >

                <ProFormText
                    name={"userId"}
                    hidden={true}
                />

                <ProFormText
                    name={"userName"}
                    label={"转交人员"}
                    fieldProps={{
                        addonAfter: (
                            <a onClick={()=>{
                                setSelectUserVisible(true);
                            }}>选人员</a>
                        )
                    }}
                    rules={[
                        {
                            required: true,
                            message: "请输入转交人员"
                        }
                    ]}
                />
            </ModalForm>


            <UserSelect
                multiple={false}
                visible={selectUserVisible}
                setVisible={setSelectUserVisible}
                onSelect={(selectedRowKeys) => {
                    if (selectedRowKeys && selectedRowKeys.length > 0) {
                        const user = selectedRowKeys[0];
                        transferForm.setFieldsValue({
                            userId: user.id,
                            userName: user.name
                        });
                        setSelectUserVisible(false);
                    }
                }}
            />


        </Modal>
    )
}


export default FlowView;

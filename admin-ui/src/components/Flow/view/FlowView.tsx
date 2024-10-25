import React from "react";
import {Button, Divider, Form, Modal, Row, Space, Tabs} from "antd";
import {detail} from "@/api/flow";
import {ProDescriptions, ProForm, ProFormTextArea} from "@ant-design/pro-components";
import moment from "moment";


interface FlowFormView {
    [key: string]: React.ComponentType;
}

interface FlowViewProps {
    id: any;
    visible: boolean;
    setVisible: (visible: boolean) => void;
    view: FlowFormView;
    review?: boolean;
    isFlowManager?: boolean;
}

const FlowView: React.FC<FlowViewProps> = (props) => {


    const [data, setData] = React.useState<any>(null);

    const [viewForm] = Form.useForm();


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
                    {props.isFlowManager && (
                        <Button
                            type={"primary"}
                            danger={true}
                        >干预</Button>
                    )}

                    {!props.review && (
                        <Button>保存</Button>
                    )}



                    {!props.review && (
                        <Button
                            type={"primary"}
                        >同意</Button>
                    )}


                    {!props.review && (
                        <Button
                            type={"primary"}
                            danger={true}
                        >拒绝</Button>
                    )}

                    {!props.review && (
                        <Button
                            type={"primary"}
                        >转办</Button>
                    )}

                    {!props.review && (
                        <Button
                            type={"primary"}
                            danger={true}
                        >延期</Button>
                    )}

                    {!props.review && (
                        <Button
                            type={"primary"}
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
                                            {data.historyRecords && data.historyRecords.filter((item:any)=>item.recodeType ==='DONE').length>0 && (
                                                <>
                                                    <Divider>
                                                        审批意见
                                                    </Divider>
                                                    {data.historyRecords && data.historyRecords.filter((item:any)=>item.recodeType ==='DONE').map((item: any) => {
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
                                                                                    {record.opinion.success?'同意':'拒绝'}: {record.opinion.advice}
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

                                        {!props.review && (
                                            <>
                                                <Divider>
                                                    审批
                                                </Divider>
                                                <ProForm
                                                    submitter={false}
                                                >
                                                    <ProFormTextArea
                                                        label={"审批意见"}
                                                        name={"advice"}
                                                    />

                                                </ProForm>
                                            </>
                                        )}

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


        </Modal>
    )
}


export default FlowView;

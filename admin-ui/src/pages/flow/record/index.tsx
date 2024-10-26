import React, {useEffect} from "react";
import {ActionType, ModalForm, PageContainer, ProFormDigit, ProFormText, ProTable} from "@ant-design/pro-components";
import {
    findDoneByOperatorId,
    findInitiatedByOperatorId,
    findPostponedTodoByOperatorId,
    findTimeoutTodoByOperatorId,
    findTodoByOperatorId,
    flowRecordList, saveFlow, urge
} from "@/api/flow";
import moment from "moment";
import {message, Tabs} from "antd";
import FlowView from "@/components/Flow/view/FlowView";
import DefaultFlowView from "@/pages/flow/leave/default";
import "./index.scss";


const FlowRecordPage = () => {

    const [flowViewVisible, setFlowViewVisible] = React.useState(false);
    const [currentId, setCurrentId] = React.useState<number>(0);
    const [reviewVisible, setReviewVisible] = React.useState(false);


    const [key, setKey] = React.useState('todo');
    const todoActionRef = React.useRef<ActionType>();
    const doneActionRef = React.useRef<ActionType>();
    const initiatedActionRef = React.useRef<ActionType>();
    const timeoutTodoActionRef = React.useRef<ActionType>();
    const postponedTodoActionRef = React.useRef<ActionType>();
    const allTodoActionRef = React.useRef<ActionType>();


    const handlerUrgeFlow = (recordId:any) => {
        const body = {
            recordId,
        }
        urge(body).then(res => {
            if (res.success) {
                message.success('催办提醒已发送').then();
                reloadTable();
            }
        })
    }

    const columns = [
        {
            title: '编号',
            dataIndex: 'id',
            search: false
        },
        {
            title: '流程编号',
            dataIndex: 'processId',
        },
        {
            title: '标题',
            dataIndex: 'title',
        },
        {
            title: '创建时间',
            dataIndex: 'createTime',
            valueType: 'dateTime'
        },
        {
            title: '是否延期',
            dataIndex: 'postponedCount',
            render: (text: any) => {
                return text > 1 ? '延期' : '-';
            }
        },
        {
            title: '超时到期时间',
            dataIndex: 'timeoutTime',
            render: (text: any) => {
                return text <= 0 ? '-' : moment(text).format("YYYY-MM-DD HH:mm:ss");
            }
        },
        {
            title: '流程发起人',
            dataIndex: 'createOperatorName',
        },
        {
            title: '流程审批人',
            dataIndex: 'currentOperatorName',
        },
        {
            title: '状态',
            dataIndex: 'flowType',
            render: (text: any) => {
                if (text === 'TODO') {
                    return '办理中';
                }
                if (text === 'DONE') {
                    return '已办理';
                }
                if (text === 'TRANSFER') {
                    return '已转办';
                }
                return text;
            }
        },
        {
            title: '办理意见',
            dataIndex: 'flowSourceDirection',
            render: (text: any) => {
                if (text === 'PASS') {
                    return '同意';
                }
                if (text === 'REJECT') {
                    return '拒绝';
                }
                if (text === 'TRANSFER') {
                    return '已转办';
                }
                return text;
            }
        },
        {
            title: '流程状态',
            dataIndex: 'flowStatus',
            render: (text: any) => {
                if (text === 'RUNNING') {
                    return '进行中';
                }
                if (text === 'FINISH') {
                    return '已结束';
                }
                return text;
            }
        },
        {
            title: '更新时间',
            dataIndex: 'updateTime',
            valueType: 'dateTime'
        },
        {
            title: '操作',
            dataIndex: 'option',
            valueType: 'option',
            render: (_: any, record: any) => {

                return [
                    <a
                        key={"detail"}
                        onClick={() => {
                            setCurrentId(record.id);
                            setReviewVisible(true);
                            setFlowViewVisible(true);
                        }}
                    >详情</a>,
                    <a
                        key={"submit"}
                        onClick={() => {
                            setCurrentId(record.id);
                            setReviewVisible(false);
                            setFlowViewVisible(true);
                        }}
                    >办理</a>,

                    <a
                        key={"urge"}
                        onClick={() => {
                            handlerUrgeFlow(record.id);
                        }}
                    >催办</a>,
                ]
            }
        }
    ] as any[];


    const reloadTable = ()=>{
        if (key === 'todo') {
            todoActionRef.current?.reload();
        }
        if (key === 'done') {
            doneActionRef.current?.reload();
        }
        if (key === 'initiated') {
            initiatedActionRef.current?.reload();
        }
        if (key === 'timeoutTodo') {
            timeoutTodoActionRef.current?.reload();
        }
        if (key === 'postponedTodo') {
            postponedTodoActionRef.current?.reload();
        }
        if (key === 'all') {
            allTodoActionRef.current?.reload();
        }
    }

    useEffect(() => {
        reloadTable();
    }, [flowViewVisible, key]);

    return (
        <PageContainer>

            <Tabs
                onChange={(key) => {
                    setKey(key);
                }}
                items={[
                    {
                        label: '我的待办',
                        key: 'todo',
                        children: (
                            <ProTable
                                actionRef={todoActionRef}
                                search={false}
                                columns={columns}
                                rowClassName={(record) => {
                                    return record.read?"record-read":"record-unread";
                                }}
                                request={async (params, sort, filter) => {
                                    return findTodoByOperatorId(params, sort, filter, []);
                                }}
                            />
                        )
                    },
                    {
                        label: '我的已办',
                        key: 'done',
                        children: (
                            <ProTable
                                actionRef={doneActionRef}
                                search={false}
                                columns={columns}
                                rowClassName={(record) => {
                                    return record.read?"record-read":"record-unread";
                                }}
                                request={async (params, sort, filter) => {
                                    return findDoneByOperatorId(params, sort, filter, []);
                                }}
                            />
                        )
                    },
                    {
                        label: '我发起的',
                        key: 'initiated',
                        children: (
                            <ProTable
                                actionRef={initiatedActionRef}
                                search={false}
                                columns={columns}
                                rowClassName={(record) => {
                                    return record.read?"record-read":"record-unread";
                                }}
                                request={async (params, sort, filter) => {
                                    return findInitiatedByOperatorId(params, sort, filter, []);
                                }}
                            />
                        )
                    },
                    {
                        label: '超时待办',
                        key: 'timeoutTodo',
                        children: (
                            <ProTable
                                actionRef={timeoutTodoActionRef}
                                search={false}
                                columns={columns}
                                rowClassName={(record) => {
                                    return record.read?"record-read":"record-unread";
                                }}
                                request={async (params, sort, filter) => {
                                    return findTimeoutTodoByOperatorId(params, sort, filter, []);
                                }}
                            />
                        )
                    },

                    {
                        label: '延期待办',
                        key: 'postponedTodo',
                        children: (
                            <ProTable
                                actionRef={postponedTodoActionRef}
                                search={false}
                                columns={columns}
                                rowClassName={(record) => {
                                    return record.read?"record-read":"record-unread";
                                }}
                                request={async (params, sort, filter) => {
                                    return findPostponedTodoByOperatorId(params, sort, filter, []);
                                }}
                            />
                        )
                    },
                    {
                        label: '全部流程',
                        key: 'all',
                        children: (
                            <ProTable
                                actionRef={allTodoActionRef}
                                search={false}
                                columns={columns}
                                rowClassName={(record) => {
                                    return record.read?"record-read":"record-unread";
                                }}
                                request={async (params, sort, filter) => {
                                    return flowRecordList(params, sort, filter, []);
                                }}
                            />
                        )
                    },
                ]}
            />

            <FlowView
                id={currentId}
                visible={flowViewVisible}
                review={reviewVisible}
                setVisible={setFlowViewVisible}
                view={{
                    'default': DefaultFlowView
                }}
            />


        </PageContainer>
    )
}


export default FlowRecordPage;

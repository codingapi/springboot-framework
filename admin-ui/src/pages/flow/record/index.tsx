import React from "react";
import {PageContainer, ProTable} from "@ant-design/pro-components";
import {
    findDoneByOperatorId,
    findInitiatedByOperatorId, findPostponedTodoByOperatorId,
    findTimeoutTodoByOperatorId,
    findTodoByOperatorId,
    flowRecordList
} from "@/api/flow";
import moment from "moment";
import {Tabs} from "antd";


const FlowRecordPage = ()=>{

    const columns = [
        {
            title: '编号',
            dataIndex: 'id',
            search: false
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
            title: '是否已读',
            dataIndex: 'read',
            render: (text: any) => {
                return text ? '已读' : '未读';
            }
        },
        {
            title: '是否延期',
            dataIndex: 'postponedCount',
            render: (text: any) => {
                return text>1 ? '延期' : '未延期';
            }
        },
        {
            title: '超时到期时间',
            dataIndex: 'timeoutTime',
            render: (text: any) => {
                return text<=0 ? '不限时间' : moment(text).format("YYYY-MM-DD HH:mm:ss");
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
            dataIndex: 'recodeType',
            render: (text: any) => {
                if(text === 'TODO'){
                    return '办理中';
                }
                if(text === 'DONE'){
                    return '已办理';
                }
                if(text === 'TRANSFER'){
                    return '已转办';
                }
                return text;
            }
        },
        {
            title: '是否干预',
            dataIndex: 'interfere',
            render: (text: any) => {
                return text ? '干预' : '未干预';
            }
        },
        {
            title: '流程状态',
            dataIndex: 'flowStatus',
            render: (text: any) => {
                if(text === 'RUNNING'){
                    return '进行中';
                }
                if(text === 'FINISH'){
                    return '已结束';
                }
                return text;
            }
        },
        {
            title: '操作',
            dataIndex: 'option',
            valueType: 'option',
            render: (_: any,record:any) => {

                return [
                    <a>详情</a>,
                    <a>办理</a>,
                    <a>转办</a>,
                    <a>保存</a>,
                    <a>撤销</a>,
                    <a>催办</a>,
                    <a>干预</a>,
                    <a>延期</a>,
                ]
            }
        }
    ] as any[];

    return (
        <PageContainer>

            <Tabs
                items={[
                    {
                        label: '我的待办',
                        key: 'todo',
                        children:(
                            <ProTable
                                search={false}
                                columns={columns}
                                request={async (params, sort, filter) => {
                                    return findTodoByOperatorId(params, sort, filter, []);
                                }}
                            />
                        )
                    },
                    {
                        label: '我的已办',
                        key: 'done',
                        children:(
                            <ProTable
                                search={false}
                                columns={columns}
                                request={async (params, sort, filter) => {
                                    return findDoneByOperatorId(params, sort, filter, []);
                                }}
                            />
                        )
                    },
                    {
                        label: '我发起的',
                        key: 'initiated',
                        children:(
                            <ProTable
                                search={false}
                                columns={columns}
                                request={async (params, sort, filter) => {
                                    return findInitiatedByOperatorId(params, sort, filter, []);
                                }}
                            />
                        )
                    },
                    {
                        label: '超时待办',
                        key: 'timeoutTodo',
                        children:(
                            <ProTable
                                search={false}
                                columns={columns}
                                request={async (params, sort, filter) => {
                                    return findTimeoutTodoByOperatorId(params, sort, filter, []);
                                }}
                            />
                        )
                    },

                    {
                        label: '延期待办',
                        key: 'postponedTodo',
                        children:(
                            <ProTable
                                search={false}
                                columns={columns}
                                request={async (params, sort, filter) => {
                                    return findPostponedTodoByOperatorId(params, sort, filter, []);
                                }}
                            />
                        )
                    },
                    {
                        label: '全部流程',
                        key: 'all',
                        children:(
                            <ProTable
                                search={false}
                                columns={columns}
                                request={async (params, sort, filter) => {
                                    return flowRecordList(params, sort, filter, []);
                                }}
                            />
                        )
                    },
                ]}
            />

        </PageContainer>
    )
}


export default FlowRecordPage;

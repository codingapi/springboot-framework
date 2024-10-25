import React from "react";
import {PageContainer, ProTable} from "@ant-design/pro-components";
import {flowRecordList} from "@/api/flow";
import moment from "moment";


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
        }
    ] as any[];

    return (
        <PageContainer>
            <ProTable
                search={false}
                columns={columns}
                request={async (params, sort, filter) => {
                    return flowRecordList(params, sort, filter, []);
                }}
            />

        </PageContainer>
    )
}


export default FlowRecordPage;

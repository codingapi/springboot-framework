import React from "react";
import {ActionType, PageContainer, ProTable} from "@ant-design/pro-components";
import {done, recall} from "@/api/flow";
import {message, Popconfirm} from "antd";

const FlowPage = () => {

    const actionType = React.useRef<ActionType>();

    const handleRecall = async (recordId: string) => {
        const res = await recall(recordId);
        if (res.success) {
            message.success("撤回成功");
        }
        actionType.current?.reload();
    }

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
            title: '操作',
            valueType: 'option',
            render: (_: any, record: any) => [
                <Popconfirm
                    key="recall"
                    title={"确认要撤回吗？"}
                    onConfirm={async () => {
                        await handleRecall(record.id);
                    }}
                >
                    <a

                    >
                        撤回
                    </a>
                </Popconfirm>

            ]
        }

    ] as any[];
    return (
        <PageContainer>

            <ProTable
                columns={columns}
                actionRef={actionType}
                rowKey={"id"}
                request={async (params, sort, filter) => {
                    const res = await done();
                    if (res.success) {
                        return {
                            data: res.data.list,
                            success: true,
                        }
                    } else {
                        return {
                            data: [],
                            success: false,
                        }
                    }
                }}
            />
        </PageContainer>
    )
};

export default FlowPage;

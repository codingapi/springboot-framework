import {list} from '@/api/user';
import type {ActionType} from '@ant-design/pro-components';
import {PageContainer, ProTable} from '@ant-design/pro-components';
import React, {useRef} from 'react';

const UserPage: React.FC = () => {

    const actionRef = useRef<ActionType>(null);

    const columns = [
        {
            title: "编号",
            dataIndex: 'id',
            key: 'id',
            sorter: true,
            search: false,
        },
        {
            title: "名称",
            dataIndex: 'name',
            key: 'name',
        },
    ];

    return (
        <PageContainer>
            <ProTable
                headerTitle="用户列表"
                actionRef={actionRef}
                columns={columns}
                rowKey="id"
                request={async (params, sort, filter) => {
                    return await list(params, sort, filter, [
                        {
                            key: 'name',
                            type: 'LIKE'
                        }
                    ]);
                }}
            />
        </PageContainer>
    );
};

export default UserPage;

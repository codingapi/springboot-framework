import React from "react";
import {ListTable} from '@visactor/react-vtable';
import {PageContainer} from "@ant-design/pro-components";

const TablePage: React.FC = () => {

    const option = {
        columns: [
            {
                field: '0',
                title: 'name'
            },
            {
                field: '1',
                title: 'age'
            },
            {
                field: '2',
                title: 'gender'
            },
            {
                field: '3',
                title: 'hobby'
            }
        ],
        records: new Array(1000).fill(['John', 18, 'male', '🏀'])
    };

    return (
        <PageContainer>
            <ListTable
                option={option}
                height={'500px'}
            />
        </PageContainer>
    )
}

export default TablePage;

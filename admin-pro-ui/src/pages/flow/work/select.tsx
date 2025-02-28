import React from "react";
import {ProTable} from "@ant-design/pro-components";
import {list} from "@/api/flow";
import {Modal} from "antd";


interface FlowSelectProps {
    multiple?: boolean;
    visible: boolean;
    setVisible: (visible: boolean) => void;
    onSelect: (record: any) => void;
}

const FlowSelect: React.FC<FlowSelectProps> = (props) => {

    const [selectedRows, setSelectedRows] = React.useState<any[]>([]);

    const columns = [
        {
            title: '编号',
            dataIndex: 'id',
            search: false,
        },
        {
            title: '标题',
            dataIndex: 'title',
        },
        {
            title: '说明',
            dataIndex: 'description',
            valueType: 'text',
            search: false,
        },
        {
            title: '创建时间',
            dataIndex: 'createTime',
            valueType: 'dateTime',
            search: false,
        },
        {
            title: '修改时间',
            dataIndex: 'updateTime',
            valueType: 'dateTime',
            search: false,
        },
        {
            title: '状态',
            dataIndex: 'enable',
            search: false,
            render: (text: any, record: any) => {
                return (
                    <>
                        {record.enable ? '启用' : '禁用'}
                    </>
                )
            }
        },

    ] as any[];
    return (
        <Modal
            width={"60%"}
            open={props.visible}
            onCancel={() => props.setVisible(false)}
            onClose={() => props.setVisible(false)}
            destroyOnClose={true}
            title={"选择流程"}
            onOk={() => {
                if (props.onSelect) {
                    if (selectedRows.length > 0) {
                        props.onSelect(selectedRows[0]);
                    }
                }
                props.setVisible(false);
            }}
        >
            <ProTable
                columns={columns}
                rowKey={"id"}
                search={false}
                rowSelection={{
                    type: 'radio',
                    selectedRowKeys:selectedRows.map(item=>item.id),
                    onChange: (selectedRowKeys,selectedRows) => {
                        setSelectedRows(selectedRows);
                    }
                }}
                request={async (params, sort, filter) => {
                    return list(params, sort, filter, []);
                }}
            />
        </Modal>
    )
};

export default FlowSelect;

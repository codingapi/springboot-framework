import React from "react";
import {ProTable} from "@ant-design/pro-components";
import {list} from "@/api/user";
import {Modal} from "antd";


interface UserSelectProps{
    multiple?: boolean;
    visible: boolean;
    setVisible: (visible: boolean) => void;
    onSelect: (record: any[]) => void;
}

const UserSelect:React.FC<UserSelectProps> = (props) => {

    const [selectedRows, setSelectedRows] = React.useState<any[]>([]);

    const columns = [
        {
            title: '编号',
            dataIndex: 'id',
            search: false
        },
        {
            title: '姓名',
            dataIndex: 'name',
        },
        {
            title: '账号名',
            dataIndex: 'username',
        },

        {
            title: '创建时间',
            dataIndex: 'createTime',
            valueType: 'dateTime',
        },

    ] as any[];
    return (
        <Modal
            width={"60%"}
            open={props.visible}
            onCancel={() => props.setVisible(false)}
            destroyOnHidden={true}
            title={"选择用户"}
            onOk={() => {
                if (props.onSelect) {
                    props.onSelect(selectedRows);
                }
                props.setVisible(false);
            }}
        >
            <ProTable
                columns={columns}
                rowKey={"id"}
                search={false}
                rowSelection={{
                    type: props.multiple ? 'checkbox' : 'radio',
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

export default UserSelect;

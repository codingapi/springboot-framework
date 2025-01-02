import React, {useEffect} from "react";
import {ActionType, EditableProTable, ProTable} from "@ant-design/pro-components";
import {useDispatch, useSelector} from "react-redux";
import {SalaryState, updateTable1} from "@/pages/salary/store/salary";
import {Table1UpdateService} from "@/pages/salary/data/table1";
import {ProColumns} from "@ant-design/pro-table/es/typing";
import {Input, InputNumber, Space} from "antd";
import AGTable from "@/pages/salary/compoments/AGTable";

const SalaryTable1 = () => {

    const users = useSelector((state: SalaryState) => state.salary.users);

    const table1 = useSelector((state: SalaryState) => state.salary.table1);

    const store = useSelector((state: SalaryState) => state.salary);

    const table2 = useSelector((state: SalaryState) => state.salary.table2);

    const table2Version = useSelector((state: SalaryState) => state.salary.table2Version);

    const actionRef = React.useRef<ActionType>(null);

    const dispatch = useDispatch();

    const table1UpdateService = new Table1UpdateService(store,
        (data) => {
            dispatch(updateTable1(data));
        }, () => {
            actionRef.current?.reload();
        });

    useEffect(() => {
        table1UpdateService.initData(users);
    }, [users]);

    // useEffect(() => {
    //     table1UpdateService.reloadData(table2);
    // }, [table2Version]);

    const columns = [
        {
            title: 'id',
            dataIndex: 'id',
            valueType: 'text',
            editable: false,
        },
        {
            title: '姓名',
            dataIndex: 'name',
            valueType: 'text',
            editable: false,
            render: (text, record) => {
                return <InputNumber
                    value={record.name}
                    defaultValue={record.name}/>
            }
        },
        {
            title: '绩效工资',
            dataIndex: 'jxgz',
            valueType: 'digit',
            editable: false,
            render: (text, record) => {
                return <InputNumber
                    value={record.jxgz}
                    defaultValue={record.jxgz}/>
            }
        },
        {
            title: '小计',
            dataIndex: 'sum',
            valueType: 'text',
            editable: false,
            render: (text, record) => {
                return <InputNumber
                    value={record.sum}
                    defaultValue={record.sum}/>
            }
        }
    ] as ProColumns[];

    return (
        <>
            {/*<ProTable*/}
            {/*    actionRef={actionRef}*/}
            {/*    columns={columns}*/}
            {/*    search={false}*/}
            {/*    rowKey={"id"}*/}
            {/*    virtual={true}*/}
            {/*    pagination={{*/}
            {/*        pageSize: 20*/}
            {/*    }}*/}
            {/*    scroll={{*/}
            {/*        x:2900,*/}
            {/*        y:800*/}
            {/*    }}*/}
            {/*    request={async () => {*/}
            {/*        return {*/}
            {/*            // table1 top 100*/}
            {/*            data: table1,*/}
            {/*            success: true*/}
            {/*        }*/}
            {/*    }}*/}
            {/*/>*/}

            <AGTable/>
        </>
    )
}

export default SalaryTable1;

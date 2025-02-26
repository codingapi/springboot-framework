import React, {useEffect} from "react";
import {ActionType, EditableProTable, ProTable} from "@ant-design/pro-components";
import {SalaryState, updateTable2, updateTable2Version} from "@/pages/salary/store/salary";
import {useDispatch, useSelector} from "react-redux";
import {Table2UpdateService} from "@/pages/salary/data/table2";
import {Input, InputNumber} from "antd";
import {ProColumns} from "@ant-design/pro-table/es/typing";
import AGTable from "@/pages/salary/compoments/AGTable";

const SalaryTable2 = () => {

    const users = useSelector((state: SalaryState) => state.salary.users);

    const table2 = useSelector((state: SalaryState) => state.salary.table1);

    const actionRef = React.useRef<ActionType>(null);

    const dispatch = useDispatch();

    // const table2UpdateService = new Table2UpdateService(table2,
    //     (data) => {
    //         dispatch(updateTable2(data));
    //     }, () => {
    //         actionRef.current?.reload();
    //     },(version)=>{
    //         dispatch(updateTable2Version(version));
    //     });

    // useEffect(() => {
    //     table2UpdateService.initData(users);
    // }, [users]);




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
            editable: true,
            // render: (text, record) => {
            //     return <Input defaultValue={record.name}/>
            // }
        },
        {
            title: '科目1',
            dataIndex: 'kemu1',
            valueType: 'digit',
            editable: true,
            // render: (text, record) => {
            //     return <InputNumber
            //         defaultValue={record.kemu1}
            //         value={record.kemu1}
            //         onBlur={(e)=>{
            //             const data = {
            //                 ...record,
            //                 kemu1: parseFloat(e.target.value)
            //             }
            //             table2UpdateService.onChange(data);
            //         }}
            //     />
            // }
        },
        {
            title: '科目2',
            dataIndex: 'kemu2',
            valueType: 'digit',
            editable: true,
            // render: (text, record) => {
            //     return <InputNumber
            //         defaultValue={record.kemu2}
            //         value={record.kemu2}
            //         onBlur={(e)=>{
            //             const data = {
            //                 ...record,
            //                 kemu2: parseFloat(e.target.value )
            //             }
            //             console.log(data)
            //             table2UpdateService.onChange(data);
            //         }}
            //     />
            // }
        },
        {
            title: '小计',
            dataIndex: 'sum',
            valueType: 'text',
            editable: true,
            // render: (text, record) => {
            //     return <InputNumber
            //         defaultValue={record.sum}
            //         value={record.sum}
            //     />
            // }
        }
    ]  as ProColumns[];


    for(let i=0;i<30;i++){
        columns.push(
            {
                title: '科目21'+i,
                dataIndex: 'kemu2'+i,
                //@ts-ignore
                valueType: 'digit'+i,
                // render: (text, record) => {
                //     return <InputNumber
                //         defaultValue={record.kemu2}
                //         value={record.kemu2}
                //         onBlur={(e)=>{
                //             const data = {
                //                 ...record,
                //                 kemu2: parseFloat(e.target.value )
                //             }
                //             console.log(data)
                //             table2UpdateService.onChange(data);
                //         }}
                //     />
                // }
            },
        )
    }

    return (
        <>
            {/*<ProTable*/}
            {/*    actionRef={actionRef}*/}
            {/*    columns={columns}*/}
            {/*    rowKey={"id"}*/}
            {/*    search={false}*/}
            {/*    virtual={true}*/}
            {/*    pagination={{*/}
            {/*        pageSize: 20*/}
            {/*    }}*/}
            {/*    scroll={{*/}
            {/*        x:5000,*/}
            {/*        y:800*/}
            {/*    }}*/}
            {/*    request={async () => {*/}
            {/*        console.log('reload table2')*/}
            {/*        return {*/}
            {/*            data: table2,*/}
            {/*            success: true*/}
            {/*        }*/}
            {/*    }}*/}
            {/*/>*/}
            <AGTable/>
        </>
    )
}

export default SalaryTable2;

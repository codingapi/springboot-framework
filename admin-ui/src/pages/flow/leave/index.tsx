import React from "react";
import Page from "@/components/Layout/Page";
import {ModalForm, ProFormDigit, ProFormTextArea, ProTable} from "@ant-design/pro-components";
import {list} from "@/api/leave";
import {Button} from "antd";


const LeavePage = ()=>{

    const [visible,setVisible] = React.useState(false);
    const columns = [
        {
            title: '编号',
            dataIndex: 'id',
            search:false
        },
        {
            title: '说明',
            dataIndex: 'desc',
        },
        {
            title: '请假天数',
            dataIndex: 'days',
        },
        {
            title: '请假人',
            dataIndex: 'userName',
        }
    ] as any[];

    return (
        <Page>
            <ProTable
                toolBarRender={()=>[
                    <Button onClick={()=>{
                        setVisible(true);
                    }}
                    >发起请假</Button>
                ]}
                columns={columns}
                search={false}
                rowKey={"id"}
                request={async (params, sort, filter) => {
                    return list(params, sort, filter, []);
                }}
            />

            <ModalForm
                title={"发起请假"}
                open={visible}
                modalProps={{
                    onCancel:()=>{
                        setVisible(false);
                    },
                    onClose:()=>{
                        setVisible(false);
                    },
                }}
                onFinish={async (values)=>{
                    console.log(values);
                }}
            >
                <ProFormTextArea
                    name={"desc"}
                    label={"请假原因"}
                />

                <ProFormDigit
                    name={"days"}
                    label={"请假天数"}
                />

            </ModalForm>

        </Page>
    )
}

export default LeavePage;

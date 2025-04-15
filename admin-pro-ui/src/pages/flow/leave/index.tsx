import React from "react";
import {ActionType, PageContainer, ProTable} from "@ant-design/pro-components";
import {list} from "@/api/leave";
import {Button} from "antd";
import LeaveForm from "@/pages/flow/leave/LeaveForm";
import FlowModelView from "@/components/flow/view/FlowModelView";

const LeavePage = () => {

    const [visible, setVisible] = React.useState(false);

    const username = localStorage.getItem('username');

    const actionRef = React.useRef<ActionType>();
    const columns = [
        {
            title: '编号',
            dataIndex: 'id',
            search: false
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
            dataIndex: 'username',
        }
    ] as any[];


    return (
        <PageContainer>
            <ProTable
                actionRef={actionRef}
                toolBarRender={() => [
                    <Button
                        type={"primary"}
                        onClick={() => {
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

            <FlowModelView
                visible={visible}
                setVisible={setVisible}
                view={LeaveForm}
                workCode={"leave"}
                formParams={{
                    clazzName: 'com.codingapi.example.infra.flow.form.LeaveForm',
                    username: username
                }}
            />

        </PageContainer>
    )
}

export default LeavePage;

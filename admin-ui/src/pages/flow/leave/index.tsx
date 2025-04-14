import React from "react";
import Page from "@/components/Layout/Page";
import {ActionType, PageContainer, ProTable} from "@ant-design/pro-components";
import {list} from "@/api/leave";
import {Button} from "antd";
import FlowView from "@/components/Flow/flow";
import LeaveForm from "@/pages/flow/leave/LeaveForm";


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
            <Page>
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

                <FlowView
                    visible={visible}
                    setVisible={setVisible}
                    view={LeaveForm}
                    workCode={"leave"}
                    formParams={{
                        clazzName: 'com.codingapi.example.infra.flow.form.LeaveForm',
                        username: username
                    }}
                />

            </Page>
        </PageContainer>
    )
}

export default LeavePage;

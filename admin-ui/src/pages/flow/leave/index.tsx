import React from "react";
import Page from "@/components/Layout/Page";
import {
    ActionType,
    ModalForm,
    PageContainer,
    ProForm,
    ProFormDigit,
    ProFormText,
    ProFormTextArea,
    ProTable
} from "@ant-design/pro-components";
import {list, startLeave} from "@/api/leave";
import {Button, message} from "antd";
import FlowSelect from "@/pages/flow/work/select";
import FlowView from "@/components/Flow/flow";
import LeaveForm from "@/pages/flow/leave/LeaveForm";


const LeavePage = () => {

    const [visible, setVisible] = React.useState(false);

    const [flowSelectVisible, setFlowSelectVisible] = React.useState(false);
    const [form] = ProForm.useForm();

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
            dataIndex: 'userName',
        }
    ] as any[];


    const handleStartFlow = async (values: any) => {
        const res = await startLeave(values);
        if (res.success) {
            message.success("发起成功,请到待办中心");
            setVisible(false);
            actionRef.current?.reload();
        }
    }

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
                        clazzName: 'com.codingapi.example.domain.Leave',
                    }}
                />

            </Page>
        </PageContainer>
    )
}

export default LeavePage;

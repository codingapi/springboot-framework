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

                <ModalForm
                    form={form}
                    title={"发起请假"}
                    open={visible}
                    modalProps={{
                        onCancel: () => {
                            setVisible(false);
                        },
                        onClose: () => {
                            setVisible(false);
                        },
                    }}
                    onFinish={async (values) => {
                        await handleStartFlow(values);
                    }}
                >

                    <ProFormText
                        name={"flowId"}
                        hidden={true}
                    />

                    <ProFormText
                        name={"flowName"}
                        label={"流程名称"}
                        disabled={true}
                        rules={[
                            {
                                required: true,
                                message: "请选择流程"
                            }
                        ]}
                        fieldProps={{
                            addonAfter: <a onClick={() => {
                                setFlowSelectVisible(true);
                            }
                            }>选择流程</a>
                        }}
                    />

                    <ProFormDigit
                        name={"days"}
                        label={"请假天数"}
                        fieldProps={{
                            step: 1
                        }}
                        rules={[
                            {
                                required: true,
                                message: "请输入请假天数"
                            }
                        ]}
                    />

                    <ProFormTextArea
                        name={"desc"}
                        label={"请假原因"}
                        rules={[
                            {
                                required: true,
                                message: "请输入请假原因"
                            }
                        ]}
                    />

                </ModalForm>

                <FlowSelect visible={flowSelectVisible} setVisible={setFlowSelectVisible} onSelect={(flow) => {

                    form.setFieldValue('flowId', flow.id);
                    form.setFieldValue('flowName', flow.title);

                }}/>

            </Page>
        </PageContainer>
    )
}

export default LeavePage;

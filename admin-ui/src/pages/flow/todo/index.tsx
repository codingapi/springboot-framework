import React from "react";
import {ActionType, PageContainer, ProFormText, ProTable} from "@ant-design/pro-components";
import {submit, todo} from "@/api/flow";
import {message, Popconfirm} from "antd";

const FlowPage = () => {

    const actionType = React.useRef<ActionType>();

    const handleSubmit=async (values:any)=>{
        const res = await submit(values);
        if(res.success){
            message.success("提交成功");
        }
        actionType.current?.reload();
    }

    const [opinion,setOpinion] = React.useState<string>("");
    const columns = [
        {
            title: '编号',
            dataIndex: 'id',
            search:false,
        },
        {
            title: '标题',
            dataIndex: 'title',
        },
        {
            title: '操作',
            valueType: 'option',
            render: (_: any, record: any) => [
                <Popconfirm
                    key="apply"
                    title={"审批"}
                    okText={"通过"}
                    cancelText={"拒绝"}
                    description={(
                        <ProFormText
                            name="description"
                            label="审批意见"
                            width="lg"
                            rules={[
                                {
                                    required: true,
                                    message: '请输入审批意见',
                                },
                            ]}
                            placeholder="请输入审批意见"
                            fieldProps={{
                                onChange:(e)=>{
                                    setOpinion(e.target.value);
                                }
                            }}
                        />
                    )}
                    onConfirm={async ()=>{
                        const body = {
                            recordId:record.id,
                            opinion:opinion,
                            pass:true
                        }
                        await handleSubmit(body);
                    }}

                    onCancel={async ()=>{
                        const body = {
                            recordId:record.id,
                            opinion:opinion,
                            pass:false
                        }
                        await handleSubmit(body);
                    }}
                >
                    <a>审批</a>
                </Popconfirm>
            ]
        }

    ] as any[];
    return (
        <PageContainer>

            <ProTable
                columns={columns}
                rowKey={"id"}
                actionRef={actionType}
                request={async (params, sort, filter) => {
                    const res = await todo();
                    if(res.success){
                        return {
                            data: res.data.list,
                            success: true,
                        }
                    }
                    return {
                        data: [],
                        success: false,
                    }

                }}
            />
        </PageContainer>
    )
};

export default FlowPage;

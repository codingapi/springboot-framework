import React from "react";
import {
    ActionType,
    ModalForm,
    PageContainer,
    ProFormDatePicker,
    ProFormSelect,
    ProFormTextArea,
    ProTable
} from "@ant-design/pro-components";
import {create, creator, list} from "@/api/flow";
import {Button, message} from "antd";

const FlowPage = () => {

    const columns = [
        {
            title: '编号',
            dataIndex: 'id',
            search:false
        },
        {
            title: '标题',
            dataIndex: 'title',
        },

    ] as any[];

    const actionRef = React.useRef<ActionType>(null);

    const handlerCreate = async (values:any)=>{
        const res = await create(values);
        if(res.success){
            message.success("发起成功");
        }
        setVisible(false);
        actionRef.current?.reload();
    }

    const [visible,setVisible] = React.useState(false);

    return (
        <PageContainer>
            <ProTable
                actionRef={actionRef}
                columns={columns}
                rowKey={"id"}
                toolBarRender={()=>{
                    return [
                        <Button
                            type={"primary"}
                            onClick={()=>{
                                setVisible(true);
                            }}
                        >
                            发起请假
                        </Button>
                    ]
                }}
                request={async (params, sort, filter) => {
                    const res = await creator();
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

            <ModalForm
                title={"发起请假"}
                open={visible}
                modalProps={{
                    destroyOnClose:true,
                    onCancel:()=>setVisible(false),
                    onClose:()=>setVisible(false),
                }}
                onFinish={handlerCreate}
            >

                <ProFormSelect
                    name={"flowWorkId"}
                    label={"流程模板"}
                    request={async ()=>{
                        const res = await list({
                            current:1,
                            pageSize:20
                        },{},{},[]);
                        return res.data.map((item:any)=>{
                            return {
                                label:item.title,
                                value:item.id
                            }
                        })
                    }}
                />

                <ProFormTextArea
                    name={"desc"}
                    label={"请假原因"}
                />

                <ProFormDatePicker
                    name={"startDate"}
                    label={"开始时间"}
                />

                <ProFormDatePicker
                    name={"endDate"}
                    label={"结束时间"}/>

            </ModalForm>

        </PageContainer>
    )
};

export default FlowPage;

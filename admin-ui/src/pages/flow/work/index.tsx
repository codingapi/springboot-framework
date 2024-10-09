import React from "react";
import Flow, {FlowActionType} from "@/components/Flow";
import {PageContainer, ProTable} from "@ant-design/pro-components";
import {list} from "@/api/flow";
import {Button, Drawer, Space} from "antd";

const FlowPage = () => {

    const [visible, setVisible] = React.useState(false);
    const flowActionType = React.useRef<FlowActionType>(null);


    const columns = [
        {
            title: '编号',
            dataIndex: 'id',
        },
        {
            title: '标题',
            dataIndex: 'title',
        },
        {
            title: '解释',
            dataIndex: 'description',
        },

    ] as any[];
    return (
       <PageContainer>

           <ProTable
               columns={columns}
               toolBarRender={()=>{
                   return [
                       <Button
                           onClick={()=>{
                               setVisible(true);
                           }}
                       >新增</Button>
                   ]
               }}
               request={async (params, sort, filter) => {
                   return list(params,sort,filter,[]);
               }}
           />


           <Drawer
               title="流程设计"
               width={"100%"}
               open={visible}
               onClose={()=>{setVisible(false)}}
               style={{
                   padding:0,
                   margin:0
               }}
               extra={
                   <Space>
                       <Button
                           type={"primary"}
                           onClick={()=>{
                               const data = flowActionType.current?.getData();
                               const json = JSON.stringify(data);
                               console.log(json);
                           }}
                       >
                           保存</Button>
                       <Button
                            onClick={()=>{
                                 setVisible(false);
                            }}
                       >取消</Button>
                   </Space>
               }
           >
               <Flow
                   actionRef={flowActionType}
               />
           </Drawer>

       </PageContainer>
    )
};

export default FlowPage;

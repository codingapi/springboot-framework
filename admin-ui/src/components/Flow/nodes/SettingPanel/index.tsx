import React from "react";
import {DrawerForm, ProFormText} from "@ant-design/pro-components";

interface SettingPanelProps {
    visible: boolean;
    setVisible: (visible: boolean) => void;
    properties: any;
    onSettingChange: (values: any) => void;
}

const SettingPanel:React.FC<SettingPanelProps> = (props)=>{

    return (
        <DrawerForm
            initialValues={props.properties}
            title={"设置面板"}
            onFinish={async (values)=>{
                props.onSettingChange(values);
                props.setVisible(false);
            }}
            drawerProps={{
                onClose: ()=>{
                    props.setVisible(false);
                },
                destroyOnClose:true
            }}
            open={props.visible}
        >
            <ProFormText
                name={"name"}
                label={"节点名称"}
                rules={[
                    {
                        required: true,
                        message: "请输入节点名称"
                    }
                ]}
            />
        </DrawerForm>
    )

}

export default SettingPanel;

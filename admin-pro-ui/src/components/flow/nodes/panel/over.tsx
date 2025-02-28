import React from "react";
import {ProForm} from "@ant-design/pro-components";
import {Button, Drawer, Space} from "antd";
import NodePanel from "@/components/flow/nodes/panel/NodePanel";
import {SettingPanelProps} from "@/components/flow/types";

const OverSettingPanel: React.FC<SettingPanelProps> = (props) => {

    const [form] = ProForm.useForm();

    return (
        <Drawer
            title={"节点设置"}
            width={"40%"}
            destroyOnClose={true}
            onClose={() => {
                props.setVisible(false);
            }}
            open={props.visible}
            extra={(
                <Space>
                    <Button
                        type={"primary"}
                        onClick={() => {
                            form.submit();
                            props.setVisible(false);
                        }}
                    >确认</Button>

                    <Button
                        onClick={() => {
                            props.setVisible(false);
                        }}
                    >关闭</Button>
                </Space>
            )}
        >
            <NodePanel
                form={form}
                type={"over"}
                id={props.properties?.id}
                data={props.properties}
                onFinish={props.onSettingChange}
            />

        </Drawer>
    )

}

export default OverSettingPanel;
